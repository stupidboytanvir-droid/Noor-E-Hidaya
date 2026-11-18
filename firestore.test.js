const test = require('node:test');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const {
  initializeTestEnvironment,
  assertSucceeds,
  assertFails,
} = require('@firebase/rules-unit-testing');

const PROJECT_ID = 'gen-lang-client-0043146128';
const DATABASE_ID = 'ai-studio-android-noorehid-1ea1b8f1-a66c-485c-ac4f-9fa502fddd8f';

let testEnv;

test.before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: fs.readFileSync('firestore.rules', 'utf8'),
      host: '127.0.0.1',
      port: 8085,
    },
  });
});

test.after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

test.beforeEach(async () => {
  await testEnv.clearFirestore();
});

test('unauthenticated users cannot read or write user profiles or monthlyHistory', async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore(DATABASE_ID);
  await assertFails(unauthDb.collection('users').doc('alice').get());
  await assertFails(
    unauthDb.collection('users').doc('alice').set({ uid: 'alice', displayName: 'Alice' })
  );
  await assertFails(
    unauthDb
      .collection('users')
      .doc('alice')
      .collection('monthlyHistory')
      .doc('2026-11')
      .set({
        yearMonth: '2026-11',
        quranMinutes: 5,
        duaCount: 3,
        tasbihCount: 33,
        duroodCount: 10,
        hadithMinutes: 4,
      })
  );
});

test('owner can read/write own profile, bookmarks, and monthlyHistory; BOLA cross-user access denied', async () => {
  const aliceDb = testEnv.authenticatedContext('alice').firestore(DATABASE_ID);
  const bobDb = testEnv.authenticatedContext('bob').firestore(DATABASE_ID);

  await assertSucceeds(
    aliceDb.collection('users').doc('alice').set({
      uid: 'alice',
      displayName: 'Alice',
      quranReadMinutes: 0,
      duasReadCount: 0,
      totalTasbihCount: 0,
      salawatJumatCount: 0,
      hadithReadMinutes: 0,
    })
  );

  await assertSucceeds(
    aliceDb
      .collection('users')
      .doc('alice')
      .collection('monthlyHistory')
      .doc('2026-11')
      .set({
        yearMonth: '2026-11',
        quranMinutes: 12,
        duaCount: 8,
        tasbihCount: 99,
        duroodCount: 33,
        hadithMinutes: 10,
        hadithReadCount: 2,
        updatedAt: 1700000000000,
      })
  );

  // Owner read succeeds
  const snap = await assertSucceeds(
    aliceDb
      .collection('users')
      .doc('alice')
      .collection('monthlyHistory')
      .doc('2026-11')
      .get()
  );
  assert.equal(snap.data().quranMinutes, 12);

  // Cross-user read & write denied (BOLA prevention)
  await assertFails(bobDb.collection('users').doc('alice').get());
  await assertFails(
    bobDb
      .collection('users')
      .doc('alice')
      .collection('monthlyHistory')
      .doc('2026-11')
      .get()
  );
  await assertFails(
    bobDb
      .collection('users')
      .doc('alice')
      .collection('monthlyHistory')
      .doc('2026-11')
      .set({
        yearMonth: '2026-11',
        quranMinutes: 999,
        duaCount: 0,
        tasbihCount: 0,
        duroodCount: 0,
        hadithMinutes: 0,
      })
  );
});

test('monthlyHistory enforces valid yearMonth format and non-negative integers', async () => {
  const aliceDb = testEnv.authenticatedContext('alice').firestore(DATABASE_ID);

  // Invalid month key format
  await assertFails(
    aliceDb
      .collection('users')
      .doc('alice')
      .collection('monthlyHistory')
      .doc('invalid-month')
      .set({
        yearMonth: 'invalid-month',
        quranMinutes: 0,
        duaCount: 0,
        tasbihCount: 0,
        duroodCount: 0,
        hadithMinutes: 0,
      })
  );

  // Negative integer value
  await assertFails(
    aliceDb
      .collection('users')
      .doc('alice')
      .collection('monthlyHistory')
      .doc('2026-11')
      .set({
        yearMonth: '2026-11',
        quranMinutes: -1,
        duaCount: 0,
        tasbihCount: 0,
        duroodCount: 0,
        hadithMinutes: 0,
      })
  );
});

test('owner can sync customZikrs and profile settings/photo while invalid schema or cross-user fails', async () => {
  const aliceDb = testEnv.authenticatedContext('alice').firestore(DATABASE_ID);
  const bobDb = testEnv.authenticatedContext('bob').firestore(DATABASE_ID);

  await assertSucceeds(
    aliceDb.collection('users').doc('alice').set({
      uid: 'alice',
      displayName: 'Alice',
      photoUrl: 'data:image/jpeg;base64,/9j/4AAQSkZJRg==',
      settings: {
        uiLanguage: 'en',
        contentLanguage: 'en',
        selectedQariId: 'alafasy',
        themeMode: 'DARK',
      },
      lastSyncedAt: 1700000000000,
    })
  );

  // Oversized photoUrl rejected (> 180000 chars)
  await assertFails(
    aliceDb.collection('users').doc('alice').set({
      uid: 'alice',
      photoUrl: 'a'.repeat(180001),
    })
  );

  await assertSucceeds(
    aliceDb
      .collection('users')
      .doc('alice')
      .collection('customZikrs')
      .doc('custom_1')
      .set({
        id: 'custom_1',
        arabicPhrase: 'سُبْحَانَ اللَّهِ',
        transliteration: 'SubhanAllah',
        bengaliMeaning: 'Glory be to Allah',
        targetCount: 33,
        currentCount: 5,
        completedRounds: 1,
        totalHistoricalCount: 38,
        updatedAt: 1700000000000,
      })
  );

  // Invalid targetCount <= 0 or mismatched ID rejected
  await assertFails(
    aliceDb
      .collection('users')
      .doc('alice')
      .collection('customZikrs')
      .doc('custom_2')
      .set({
        id: 'custom_2',
        arabicPhrase: 'الحمد لله',
        targetCount: 0,
      })
  );

  // Cross-user access denied
  await assertFails(
    bobDb
      .collection('users')
      .doc('alice')
      .collection('customZikrs')
      .doc('custom_1')
      .get()
  );
  await assertFails(
    bobDb
      .collection('users')
      .doc('alice')
      .collection('customZikrs')
      .doc('custom_1')
      .delete()
  );
});
