package com.example.ui.theme

import com.example.data.util.SupportedLanguage

/**
 * Global multi-language translation registry supporting all 12 official languages:
 * bn, en, ar, ur, id, ms, hi, tr, de, fr, sq, bs.
 *
 * Non-Bengali languages NEVER fall back to Bengali — if a key isn't in the target language
 * dictionary yet, it falls back cleanly to English.
 */
object LocalizationRegistry {

    private data class LocalizedPack(
        val ar: String? = null,
        val ur: String? = null,
        val id: String? = null,
        val ms: String? = null,
        val hi: String? = null,
        val tr: String? = null,
        val de: String? = null,
        val fr: String? = null,
        val sq: String? = null,
        val bs: String? = null
    ) {
        fun forLang(lang: SupportedLanguage): String? = when (lang) {
            SupportedLanguage.ARABIC -> ar
            SupportedLanguage.URDU -> ur
            SupportedLanguage.INDONESIAN -> id
            SupportedLanguage.MALAY -> ms
            SupportedLanguage.HINDI -> hi
            SupportedLanguage.TURKISH -> tr
            SupportedLanguage.GERMAN -> de
            SupportedLanguage.FRENCH -> fr
            SupportedLanguage.ALBANIAN -> sq
            SupportedLanguage.BOSNIAN -> bs
            else -> null
        }
    }

    private fun pack(
        ar: String? = null,
        ur: String? = null,
        id: String? = null,
        ms: String? = null,
        hi: String? = null,
        tr: String? = null,
        de: String? = null,
        fr: String? = null,
        sq: String? = null,
        bs: String? = null
    ) = LocalizedPack(ar, ur, id, ms, hi, tr, de, fr, sq, bs)

    private val entries: Map<String, LocalizedPack> = mapOf(
        // Branding & Tabs
        "Noor-e-Hidayah" to pack(
            ar = "نور الهداية", ur = "نورِ ہدایت", id = "Noor-e-Hidayah", ms = "Noor-e-Hidayah",
            hi = "नूर-ए-हिदायत", tr = "Noor-e-Hidayah", de = "Noor-e-Hidayah", fr = "Noor-e-Hidayah",
            sq = "Noor-e-Hidayah", bs = "Noor-e-Hidayah"
        ),
        "Home" to pack(
            ar = "الرئيسية", ur = "ہوم", id = "Beranda", ms = "Utama",
            hi = "होम", tr = "Ana Sayfa", de = "Start", fr = "Accueil",
            sq = "Kryefaqja", bs = "Početna"
        ),
        "Quran" to pack(
            ar = "القرآن", ur = "قرآن", id = "Al-Qur'an", ms = "Al-Quran",
            hi = "क़ुरआन", tr = "Kur'an", de = "Koran", fr = "Coran",
            sq = "Kur'ani", bs = "Kur'an"
        ),
        "Du'a" to pack(
            ar = "الدعاء", ur = "دعائیں", id = "Doa", ms = "Doa",
            hi = "दुआ", tr = "Dua", de = "Dua", fr = "Doua",
            sq = "Dua", bs = "Dova"
        ),
        "Dua" to pack(
            ar = "الدعاء", ur = "دعائیں", id = "Doa", ms = "Doa",
            hi = "दुआ", tr = "Dua", de = "Dua", fr = "Doua",
            sq = "Dua", bs = "Dova"
        ),
        "Tools" to pack(
            ar = "الأدوات", ur = "ٹولز", id = "Alat", ms = "Alatan",
            hi = "टूल्स", tr = "Araçlar", de = "Werkzeuge", fr = "Outils",
            sq = "Mjete", bs = "Alati"
        ),
        "More" to pack(
            ar = "المزيد", ur = "مزید", id = "Lainnya", ms = "Lagi",
            hi = "अधिक", tr = "Daha Fazla", de = "Mehr", fr = "Plus",
            sq = "Më shumë", bs = "Više"
        ),
        "Explore" to pack(
            ar = "استكشاف", ur = "دریافت", id = "Jelajah", ms = "Teroka",
            hi = "खोजें", tr = "Keşfet", de = "Entdecken", fr = "Explorer",
            sq = "Zbulo", bs = "Istraži"
        ),

        // Common UI Actions & Status
        "Save" to pack(
            ar = "حفظ", ur = "محفوظ کریں", id = "Simpan", ms = "Simpan",
            hi = "सहेजें", tr = "Kaydet", de = "Speichern", fr = "Enregistrer",
            sq = "Ruaj", bs = "Sačuvaj"
        ),
        "Cancel" to pack(
            ar = "إلغاء", ur = "منسوخ کریں", id = "Batal", ms = "Batal",
            hi = "रद्द करें", tr = "İptal", de = "Abbrechen", fr = "Annuler",
            sq = "Anulo", bs = "Otkaži"
        ),
        "Continue" to pack(
            ar = "متابعة", ur = "جاری رکھیں", id = "Lanjutkan", ms = "Teruskan",
            hi = "जारी रखें", tr = "Devam Et", de = "Weiter", fr = "Continuer",
            sq = "Vazhdo", bs = "Nastavi"
        ),
        "Close" to pack(
            ar = "إغلاق", ur = "بند کریں", id = "Tutup", ms = "Tutup",
            hi = "बंद करें", tr = "Kapat", de = "Schließen", fr = "Fermer",
            sq = "Mbyll", bs = "Zatvori"
        ),
        "Done" to pack(
            ar = "تم", ur = "مکمل", id = "Selesai", ms = "Selesai",
            hi = "हो गया", tr = "Tamam", de = "Fertig", fr = "Terminé",
            sq = "U krye", bs = "Gotovo"
        ),
        "Reset" to pack(
            ar = "إعادة ضبط", ur = "ری سیٹ", id = "Atur Ulang", ms = "Tetap Semula",
            hi = "रीसेट", tr = "Sıfırla", de = "Zurücksetzen", fr = "Réinitialiser",
            sq = "Rivendos", bs = "Resetuj"
        ),
        "Retry" to pack(
            ar = "إعادة المحاولة", ur = "دوبارہ کوشش", id = "Coba Lagi", ms = "Cuba Lagi",
            hi = "पुनः प्रयास", tr = "Tekrar Dene", de = "Wiederholen", fr = "Réessayer",
            sq = "Provo përsëri", bs = "Pokušaj ponovo"
        ),
        "Apply" to pack(
            ar = "تطبيق", ur = "لاگو کریں", id = "Terapkan", ms = "Gunakan",
            hi = "लागू करें", tr = "Uygula", de = "Anwenden", fr = "Appliquer",
            sq = "Zbato", bs = "Primijeni"
        ),
        "Edit" to pack(
            ar = "تعديل", ur = "ترمیم", id = "Ubah", ms = "Sunting",
            hi = "संपादित करें", tr = "Düzenle", de = "Bearbeiten", fr = "Modifier",
            sq = "Ndrysho", bs = "Uredi"
        ),
        "Delete" to pack(
            ar = "حذف", ur = "حذف کریں", id = "Hapus", ms = "Padam",
            hi = "हटाएं", tr = "Sil", de = "Löschen", fr = "Supprimer",
            sq = "Fshij", bs = "Obriši"
        ),
        "Share" to pack(
            ar = "مشاركة", ur = "شیئر کریں", id = "Bagikan", ms = "Kongsi",
            hi = "साझा करें", tr = "Paylaş", de = "Teilen", fr = "Partager",
            sq = "Ndaj", bs = "Podijeli"
        ),
        "Copy" to pack(
            ar = "نسخ", ur = "کاپی کریں", id = "Salin", ms = "Salin",
            hi = "कॉपी", tr = "Kopyala", de = "Kopieren", fr = "Copier",
            sq = "Kopjo", bs = "Kopiraj"
        ),
        "Search" to pack(
            ar = "بحث", ur = "تلاش کریں", id = "Cari", ms = "Cari",
            hi = "खोजें", tr = "Ara", de = "Suchen", fr = "Rechercher",
            sq = "Kërko", bs = "Pretraži"
        ),
        "All" to pack(
            ar = "الكل", ur = "سب", id = "Semua", ms = "Semua",
            hi = "सभी", tr = "Tümü", de = "Alle", fr = "Tout",
            sq = "Të gjitha", bs = "Sve"
        ),
        "Loading..." to pack(
            ar = "جار التحميل...", ur = "لوڈ ہو رہا ہے...", id = "Memuat...", ms = "Memuatkan...",
            hi = "लोड हो रहा है...", tr = "Yükleniyor...", de = "Laden...", fr = "Chargement...",
            sq = "Duke u ngarkuar...", bs = "Učitavanje..."
        ),
        "Active" to pack(
            ar = "نشط", ur = "فعال", id = "Aktif", ms = "Aktif",
            hi = "सक्रिय", tr = "Etkin", de = "Aktiv", fr = "Actif",
            sq = "Aktiv", bs = "Aktivan"
        ),
        "Enabled" to pack(
            ar = "مفعّل", ur = "فعال", id = "Diaktifkan", ms = "Didayakan",
            hi = "सक्रिय", tr = "Açık", de = "Aktiviert", fr = "Activé",
            sq = "Aktivizuar", bs = "Omogućeno"
        ),
        "Disabled" to pack(
            ar = "معطّل", ur = "غیر فعال", id = "Dinonaktifkan", ms = "Dilumpuhkan",
            hi = "निष्क्रिय", tr = "Kapalı", de = "Deaktiviert", fr = "Désactivé",
            sq = "Çaktivizuar", bs = "Onemogućeno"
        ),
        "Go" to pack(
            ar = "انتقال", ur = "جائیں", id = "Buka", ms = "Pergi",
            hi = "जाएं", tr = "Git", de = "Los", fr = "Aller",
            sq = "Shko", bs = "Idi"
        ),

        // Welcome & Onboarding Language Picker
        "Choose Your Language" to pack(
            ar = "اختر لغتك", ur = "اپنی زبان منتخب کریں", id = "Pilih Bahasa Anda", ms = "Pilih Bahasa Anda",
            hi = "अपनी भाषा चुनें", tr = "Dilinizi Seçin", de = "Wählen Sie Ihre Sprache", fr = "Choisissez votre langue",
            sq = "Zgjidhni gjuhën tuaj", bs = "Odaberite svoj jezik"
        ),
        "Select your preferred app interface and translation language to personalize Noor-e-Hidayah." to pack(
            ar = "اختر لغة التطبيق والترجمة المفضلة لتخصيص نور الهداية.",
            ur = "نورِ ہدایت کو اپنی پسند کے مطابق بنانے کے لیے ایپ اور ترجمے کی زبان منتخب کریں۔",
            id = "Pilih bahasa aplikasi dan terjemahan pilihan Anda untuk mempersonalisasi Noor-e-Hidayah.",
            ms = "Pilih bahasa aplikasi dan terjemahan pilihan anda untuk memperibadikan Noor-e-Hidayah.",
            hi = "नूर-ए-हिदायत को निजीकृत करने के लिए अपनी पसंदीदा ऐप और अनुवाद भाषा चुनें।",
            tr = "Noor-e-Hidayah deneyiminizi kişiselleştirmek için uygulama ve meal dilinizi seçin.",
            de = "Wählen Sie Ihre bevorzugte App- und Übersetzungssprache zur Personalisierung von Noor-e-Hidayah.",
            fr = "Sélectionnez la langue de l'application et de la traduction pour personnaliser Noor-e-Hidayah.",
            sq = "Zgjidhni gjuhën e aplikacionit dhe të përkthimit për të personalizuar Noor-e-Hidayah.",
            bs = "Odaberite željeni jezik aplikacije i prijevoda da prilagodite Noor-e-Hidayah."
        ),
        "Search language..." to pack(
            ar = "ابحث عن لغة...", ur = "زبان تلاش کریں...", id = "Cari bahasa...", ms = "Cari bahasa...",
            hi = "भाषा खोजें...", tr = "Dil ara...", de = "Sprache suchen...", fr = "Rechercher une langue...",
            sq = "Kërko gjuhën...", bs = "Pretraži jezik..."
        ),
        "Welcome to Noor-e-Hidayah" to pack(
            ar = "مرحبًا بكم في نور الهداية", ur = "نورِ ہدایت میں خوش آمدید",
            id = "Selamat datang di Noor-e-Hidayah", ms = "Selamat datang ke Noor-e-Hidayah",
            hi = "नूर-ए-हिदायत में आपका स्वागत है", tr = "Noor-e-Hidayah'a Hoş Geldiniz",
            de = "Willkommen bei Noor-e-Hidayah", fr = "Bienvenue dans Noor-e-Hidayah",
            sq = "Mirë se vini në Noor-e-Hidayah", bs = "Dobrodošli u Noor-e-Hidayah"
        ),
        "Get Started" to pack(
            ar = "ابدأ الآن", ur = "شروع کریں", id = "Mulai", ms = "Mula Sekarang",
            hi = "शुरू करें", tr = "Başla", de = "Loslegen", fr = "Commencer",
            sq = "Fillo", bs = "Započni"
        ),
        "Grant Permissions" to pack(
            ar = "منح الأذونات", ur = "اجازتیں دیں", id = "Berikan Izin", ms = "Berikan Kebenaran",
            hi = "अनुमतियां दें", tr = "İzinleri Ver", de = "Berechtigungen erteilen", fr = "Accorder les autorisations",
            sq = "Jep lejet", bs = "Odobri dozvole"
        ),

        // Prayer Schedule & Home Dashboard
        "Today's Prayer Times" to pack(
            ar = "مواقيت الصلاة اليوم", ur = "آج کے نماز کے اوقات", id = "Jadwal Sholat Hari Ini", ms = "Waktu Solat Hari Ini",
            hi = "आज के नमाज़ के समय", tr = "Bugünün Namaz Vakitleri", de = "Heutige Gebetszeiten", fr = "Horaires de prière du jour",
            sq = "Kohët e namazit sot", bs = "Današnja vaktija"
        ),
        "Next Prayer" to pack(
            ar = "الصلاة القادمة", ur = "اگلی نماز", id = "Sholat Berikutnya", ms = "Solat Seterusnya",
            hi = "अगली नमाज़", tr = "Sonraki Vakit", de = "Nächstes Gebet", fr = "Prochaine prière",
            sq = "Namazi i ardhshëm", bs = "Sljedeći namaz"
        ),
        "Current Waqt" to pack(
            ar = "الوقت الحالي", ur = "موجودہ وقت", id = "Waktu Saat Ini", ms = "Waktu Semasa",
            hi = "वर्तमान समय", tr = "Şu Anki Vakit", de = "Aktuelle Gebetszeit", fr = "Prière actuelle",
            sq = "Koha aktuale", bs = "Trenutni vakat"
        ),
        "Sunrise" to pack(
            ar = "الشروق", ur = "طلوعِ آفتاب", id = "Terbit", ms = "Syuruk",
            hi = "सूर्योदय", tr = "Güneş", de = "Sonnenaufgang", fr = "Lever du soleil",
            sq = "Lindja e diellit", bs = "Izlazak sunca"
        ),
        "Sunset" to pack(
            ar = "الغروب", ur = "غروبِ آفتاب", id = "Terbenam", ms = "Terbenam",
            hi = "सूर्यास्त", tr = "Gün Batımı", de = "Sonnenuntergang", fr = "Coucher du soleil",
            sq = "Perëndimi", bs = "Zalazak sunca"
        ),
        "Sahur Ends" to pack(
            ar = "نهاية السحور", ur = "اختتامِ سحری", id = "Batas Sahur", ms = "Tamat Sahur",
            hi = "सहरी समाप्त", tr = "Sahur Bitişi", de = "Sahur-Ende", fr = "Fin du Sahur",
            sq = "Fundi i syfyrit", bs = "Kraj sehura"
        ),
        "Iftar Time" to pack(
            ar = "وقت الإفطار", ur = "وقتِ افطار", id = "Waktu Berbuka", ms = "Waktu Berbuka",
            hi = "इफ़्तार का समय", tr = "İftar Vakti", de = "Iftar-Zeit", fr = "Heure de l'Iftar",
            sq = "Koha e iftarit", bs = "Vrijeme iftara"
        ),
        "Prohibited Prayer Times" to pack(
            ar = "أوقات النهي عن الصلاة", ur = "مکروہ اوقاتِ نماز", id = "Waktu Terlarang Sholat", ms = "Waktu Haram Solat",
            hi = "नमाज़ के निषिद्ध समय", tr = "Kerahet Vakitleri", de = "Verbotene Gebetszeiten", fr = "Heures interdites pour prier",
            sq = "Kohët e ndaluara të namazit", bs = "Mekruh vremena za namaz"
        ),
        "Makruh Times" to pack(
            ar = "أوقات الكراهة", ur = "مکروہ اوقات", id = "Waktu Makruh", ms = "Waktu Makruh",
            hi = "मकरूह समय", tr = "Kerahet Vakitleri", de = "Makruh-Zeiten", fr = "Heures Makruh",
            sq = "Kohët mekruh", bs = "Mekruh vremena"
        ),
        "Nafal Prayer Times" to pack(
            ar = "أوقات صلاة النوافل", ur = "نوافل کے اوقات", id = "Jadwal Sholat Sunnah", ms = "Waktu Solat Sunat",
            hi = "नफ़्ल नमाज़ के समय", tr = "Nafile Namaz Vakitleri", de = "Freiwillige Gebetszeiten", fr = "Horaires des prières surérogatoires",
            sq = "Kohët e namazeve nafile", bs = "Vremena nafila namaza"
        ),
        "Today's Nasihah" to pack(
            ar = "نصيحة اليوم", ur = "آج کی نصیحت", id = "Nasihat Hari Ini", ms = "Nasihat Hari Ini",
            hi = "आज की नसीहत", tr = "Günün Nasihati", de = "Heutiger Ratschlag", fr = "Conseil du jour",
            sq = "Këshilla e ditës", bs = "Savjet dana"
        ),
        "Daily Special" to pack(
            ar = "رسالة اليوم الخاصة", ur = "آج کا خاص پیغام", id = "Pesan Khusus Hari Ini", ms = "Mesej Khas Hari Ini",
            hi = "आज का विशेष संदेश", tr = "Günün Özel Mesajı", de = "Tagesbotschaft", fr = "Message spécial du jour",
            sq = "Mesazhi special i ditës", bs = "Posebna poruka dana"
        ),
        "Jumu'ah Mubarak" to pack(
            ar = "جمعة مباركة", ur = "جمعہ مبارک", id = "Jumat Berkah", ms = "Jumaat Mubarak",
            hi = "जुमुआ मुबारक", tr = "Hayırlı Cumalar", de = "Gesegneter Freitag", fr = "Jumu'ah Moubarak",
            sq = "Xhumaja e bekuar", bs = "Džuma mubarek"
        ),
        "Today's Hadith" to pack(
            ar = "حديث اليوم", ur = "آج کی حدیث", id = "Hadits Hari Ini", ms = "Hadis Hari Ini",
            hi = "आज की हदीस", tr = "Günün Hadisi", de = "Hadith des Tages", fr = "Hadith du jour",
            sq = "Hadithi i ditës", bs = "Hadis dana"
        ),
        "Hadith of the Day" to pack(
            ar = "حديث اليوم", ur = "آج کی حدیث", id = "Hadits Hari Ini", ms = "Hadis Hari Ini",
            hi = "आज की हदीस", tr = "Günün Hadisi", de = "Hadith des Tages", fr = "Hadith du jour",
            sq = "Hadithi i ditës", bs = "Hadis dana"
        ),
        "Daily Tadabbur" to pack(
            ar = "تدبر اليوم", ur = "آج کا تدبر", id = "Tadabbur Harian", ms = "Tadabbur Harian",
            hi = "दैनिक तदब्बुर", tr = "Günlük Tefekkür", de = "Tägliche Besinnung", fr = "Méditation du jour",
            sq = "Tadabburi ditor", bs = "Dnevni tedebbur"
        ),
        "Daily Amal Hub" to pack(
            ar = "متابعة الأعمال اليومية", ur = "روزانہ کے اعمال", id = "Amalan Harian", ms = "Amalan Harian",
            hi = "दैनिक अमल", tr = "Günlük Ameller", de = "Tägliche Taten", fr = "Bonnes actions quotidiennes",
            sq = "Veprat ditore", bs = "Dnevni ibadeti"
        ),
        "Spiritual Habit Insights" to pack(
            ar = "إحصائيات العادات الروحية", ur = "روحانی عادات کا جائزہ", id = "Wawasan Ibadah", ms = "Statistik Ibadah",
            hi = "आध्यात्मिक आदत विश्लेषण", tr = "İbadet İstatistikleri", de = "Spirituelle Einblicke", fr = "Aperçu spirituel",
            sq = "Statistikat shpirtërore", bs = "Uvid u ibadete"
        ),
        "Quick Access" to pack(
            ar = "وصول سريع", ur = "فوری رسائی", id = "Akses Cepat", ms = "Akses Pantas",
            hi = "त्वरित पहुंच", tr = "Hızlı Erişim", de = "Schnellzugriff", fr = "Accès rapide",
            sq = "Qasje e shpejtë", bs = "Brzi pristup"
        ),
        "Prayer Tracker" to pack(
            ar = "متتبع الصلاة", ur = "نماز ٹریکر", id = "Pelacak Sholat", ms = "Penjejak Solat",
            hi = "नमाज़ ट्रैकर", tr = "Namaz Takibi", de = "Gebetstracker", fr = "Suivi des prières",
            sq = "Ndjekësi i namazit", bs = "Praćenje namaza"
        ),
        "Qibla" to pack(
            ar = "القبلة", ur = "قبلہ", id = "Kiblat", ms = "Kiblat",
            hi = "क़िबला", tr = "Kıble", de = "Qibla", fr = "Qibla",
            sq = "Kibla", bs = "Kibla"
        ),
        "Qibla Compass" to pack(
            ar = "بوصلة القبلة", ur = "قبلہ کمپاس", id = "Kompas Kiblat", ms = "Kompas Kiblat",
            hi = "क़िबला कंपास", tr = "Kıble Pusulası", de = "Qibla-Kompass", fr = "Boussole Qibla",
            sq = "Busulla e Kiblës", bs = "Kibla kompas"
        ),
        "Tasbeeh" to pack(
            ar = "التسبيح", ur = "تسبیح", id = "Tasbih", ms = "Tasbih",
            hi = "तस्बीह", tr = "Tesbih", de = "Tasbih", fr = "Tasbih",
            sq = "Tesbih", bs = "Tesbih"
        ),
        "99 Names" to pack(
            ar = "أسماء الله الحسنى", ur = "اسمائے حسنیٰ", id = "Asmaul Husna", ms = "99 Nama Allah",
            hi = "99 नाम", tr = "Esmaül Hüsna", de = "99 Namen Allahs", fr = "99 Noms d'Allah",
            sq = "99 Emrat", bs = "99 Allahovih imena"
        ),
        "Ruqyah" to pack(
            ar = "الرقية الشرعية", ur = "رقیہ شرعیہ", id = "Ruqyah", ms = "Ruqyah",
            hi = "रुक़या", tr = "Rukye", de = "Ruqyah", fr = "Roqya",
            sq = "Rukje", bs = "Rukja"
        ),
        "Tilawat" to pack(
            ar = "التلاوة", ur = "تلاوت", id = "Tilawah", ms = "Tilawah",
            hi = "तिलावत", tr = "Tilavet", de = "Rezitation", fr = "Récitation",
            sq = "Tilavet", bs = "Tilavet"
        ),
        "Tafsir" to pack(
            ar = "التفسير", ur = "تفسیر", id = "Tafsir", ms = "Tafsir",
            hi = "तफ़सीर", tr = "Tefsir", de = "Tafsir", fr = "Tafsir",
            sq = "Tefsir", bs = "Tefsir"
        ),
        "Zakat" to pack(
            ar = "الزكاة", ur = "زکوٰۃ", id = "Zakat", ms = "Zakat",
            hi = "ज़कात", tr = "Zekat", de = "Zakat", fr = "Zakat",
            sq = "Zekati", bs = "Zekat"
        ),
        "Zakat Calculator" to pack(
            ar = "حاسبة الزكاة", ur = "زکوٰۃ کیلکولیٹر", id = "Kalkulator Zakat", ms = "Kalkulator Zakat",
            hi = "ज़कात कैलकुलेटर", tr = "Zekat Hesaplayıcı", de = "Zakat-Rechner", fr = "Calculateur de Zakat",
            sq = "Llogaritësi i Zekatit", bs = "Kalkulator zekata"
        ),

        // Audio & Quran & Dua
        "Playing Audio" to pack(
            ar = "جارٍ التشغيل", ur = "آڈیو چل رہی ہے", id = "Memutar Audio", ms = "Memainkan Audio",
            hi = "ऑडियो चल रहा है", tr = "Ses Çalınıyor", de = "Wiedergabe läuft", fr = "Lecture audio",
            sq = "Po luhet audio", bs = "Reprodukcija zvuka"
        ),
        "Buffering..." to pack(
            ar = "جارٍ التحميل...", ur = "بفرنگ ہو رہی ہے...", id = "Menyangga...", ms = "Menimbal...",
            hi = "बफ़र हो रहा है...", tr = "Arabelleğe Alınıyor...", de = "Puffern...", fr = "Mise en mémoire tampon...",
            sq = "Duke ngarkuar...", bs = "Baferovanje..."
        ),
        "Paused" to pack(
            ar = "متوقف مؤقتًا", ur = "رکا ہوا ہے", id = "Dijeda", ms = "Dijeda",
            hi = "रुका हुआ", tr = "Duraklatıldı", de = "Pausiert", fr = "En pause",
            sq = "E ndalur", bs = "Pauzirano"
        ),
        "Play" to pack(
            ar = "تشغيل", ur = "چلائیں", id = "Putar", ms = "Main",
            hi = "चलाएं", tr = "Oynat", de = "Abspielen", fr = "Lire",
            sq = "Luaj", bs = "Pusti"
        ),
        "Pause" to pack(
            ar = "إيقاف مؤقت", ur = "روکیں", id = "Jeda", ms = "Jeda",
            hi = "रोकें", tr = "Duraklat", de = "Pause", fr = "Pause",
            sq = "Ndalo", bs = "Pauziraj"
        ),
        "Stop" to pack(
            ar = "إيقاف", ur = "بند کریں", id = "Hentikan", ms = "Berhenti",
            hi = "रोकें", tr = "Durdur", de = "Stopp", fr = "Arrêter",
            sq = "Ndalo", bs = "Zaustavi"
        ),
        "Download Surah" to pack(
            ar = "تنزيل السورة", ur = "سورت ڈاؤن لوڈ کریں", id = "Unduh Surah", ms = "Muat Turun Surah",
            hi = "सूरा डाउनलोड करें", tr = "Sureyi İndir", de = "Sure herunterladen", fr = "Télécharger la sourate",
            sq = "Shkarko suren", bs = "Preuzmi suru"
        ),
        "Downloaded" to pack(
            ar = "تم التنزيل", ur = "ڈاؤن لوڈ شدہ", id = "Terunduh", ms = "Dimuat Turun",
            hi = "डाउनलोड हो गया", tr = "İndirildi", de = "Heruntergeladen", fr = "Téléchargé",
            sq = "Shkarkuar", bs = "Preuzeto"
        ),
        "Downloading..." to pack(
            ar = "جارٍ التنزيل...", ur = "ڈاؤن لوڈ ہو رہا ہے...", id = "Mengunduh...", ms = "Memuat turun...",
            hi = "डाउनलोड हो रहा है...", tr = "İndiriliyor...", de = "Wird heruntergeladen...", fr = "Téléchargement...",
            sq = "Duke shkarkuar...", bs = "Preuzimanje..."
        ),
        "Offline Ready" to pack(
            ar = "متاح بدون إنترنت", ur = "آف لائن دستیاب", id = "Siap Offline", ms = "Sedia Luar Talian",
            hi = "ऑफ़लाइन तैयार", tr = "Çevrimdışı Hazır", de = "Offline verfügbar", fr = "Disponible hors ligne",
            sq = "Gati jashtë linje", bs = "Dostupno van mreže"
        ),
        "Reciter (Qari)" to pack(
            ar = "القارئ", ur = "قاری", id = "Qari", ms = "Qari",
            hi = "क़ारी", tr = "Kari", de = "Rezitator", fr = "Récitateur",
            sq = "Recituesi", bs = "Učač"
        ),
        "Arabic Pronunciation" to pack(
            ar = "النطق العربي", ur = "عربی تلفظ", id = "Pelafalan Arab", ms = "Sebutan Arab",
            hi = "अरबी उच्चारण", tr = "Arapça Telaffuz", de = "Arabische Aussprache", fr = "Prononciation arabe",
            sq = "Shqiptimi arabisht", bs = "Arapski izgovor"
        ),

        // Settings & More Dialogs
        "Language Preferences" to pack(
            ar = "تفضيلات اللغة", ur = "زبان کی ترتیبات", id = "Preferensi Bahasa", ms = "Tetapan Bahasa",
            hi = "भाषा प्राथमिकताएं", tr = "Dil Tercihleri", de = "Spracheinstellungen", fr = "Préférences de langue",
            sq = "Preferencat e gjuhës", bs = "Postavke jezika"
        ),
        "App Interface Language" to pack(
            ar = "لغة واجهة التطبيق", ur = "ایپ انٹرفیس کی زبان", id = "Bahasa Antarmuka Aplikasi", ms = "Bahasa Antara Muka Aplikasi",
            hi = "ऐप इंटरफ़ेस भाषा", tr = "Uygulama Arayüz Dili", de = "App-Oberflächensprache", fr = "Langue de l'interface",
            sq = "Gjuha e ndërfaqes së aplikacionit", bs = "Jezik sučelja aplikacije"
        ),
        "Quran & Du'a Content Language" to pack(
            ar = "لغة ترجمة القرآن والدعاء", ur = "قرآن و دعا کے ترجمے کی زبان", id = "Bahasa Terjemahan Al-Qur'an & Doa", ms = "Bahasa Terjemahan Al-Quran & Doa",
            hi = "क़ुरआन और दुआ अनुवाद भाषा", tr = "Kur'an ve Dua Çeviri Dili", de = "Koran- & Dua-Übersetzungssprache", fr = "Langue de traduction du Coran & Douas",
            sq = "Gjuha e përkthimit të Kur'anit dhe Duave", bs = "Jezik prijevoda Kur'ana i dova"
        ),
        "Mosque Mode" to pack(
            ar = "وضع المسجد", ur = "مسجد موڈ", id = "Mode Masjid", ms = "Mod Masjid",
            hi = "मस्जिद मोड", tr = "Cami Modu", de = "Moschee-Modus", fr = "Mode Mosquée",
            sq = "Modaliteti i xhamisë", bs = "Džamijski režim"
        ),
        "Auto-SMS Reply" to pack(
            ar = "الرد التلقائي بالرسائل", ur = "آٹو ایس ایم ایس جواب", id = "Balas SMS Otomatis", ms = "Balas SMS Automatik",
            hi = "ऑटो-एसएमएस उत्तर", tr = "Otomatik SMS Yanıtı", de = "Automatische SMS-Antwort", fr = "Réponse SMS automatique",
            sq = "Përgjigje automatike me SMS", bs = "Automatski SMS odgovor"
        ),
        "Noor Islamic AI" to pack(
            ar = "المساعد الإسلامي الذكي", ur = "نور اسلامی اے آئی", id = "Noor Asisten Islami", ms = "Noor Pembantu Islamik",
            hi = "नूर इस्लामिक एआई", tr = "Noor İslami Yapay Zeka", de = "Noor Islamischer Assistent", fr = "Noor Assistant Islamique",
            sq = "Noor Asistenti Islam", bs = "Noor Islamski AI"
        ),
        "Audio & Storage" to pack(
            ar = "الصوت والتخزين", ur = "آڈیو اور اسٹوریج", id = "Audio & Penyimpanan", ms = "Audio & Storan",
            hi = "ऑडियो और स्टोरेज", tr = "Ses ve Depolama", de = "Audio & Speicher", fr = "Audio et stockage",
            sq = "Audio dhe memoria", bs = "Audio i pohrana"
        ),
        "Clear Audio Cache" to pack(
            ar = "مسح ذاكرة الصوت المؤقتة", ur = "آڈیو کیش صاف کریں", id = "Hapus Cache Audio", ms = "Kosongkan Cache Audio",
            hi = "ऑडियो कैश साफ़ करें", tr = "Ses Önbelleğini Temizle", de = "Audio-Cache leeren", fr = "Vider le cache audio",
            sq = "Pastro memoren e audios", bs = "Obriši audio keš"
        ),
        "Hadith Collections" to pack(
            ar = "موسوعة الحديث", ur = "کتبِ احادیث", id = "Koleksi Hadits", ms = "Koleksi Hadis",
            hi = "हदीस संग्रह", tr = "Hadis Külliyatı", de = "Hadith-Sammlungen", fr = "Collections de Hadiths",
            sq = "Koleksionet e Haditheve", bs = "Zbirke hadisa"
        ),
        "Hijri Calendar" to pack(
            ar = "التقويم الهجري", ur = "ہجری کیلنڈر", id = "Kalender Hijriah", ms = "Kalendar Hijrah",
            hi = "हिजरी कैलेंडर", tr = "Hicri Takvim", de = "Hijri-Kalender", fr = "Calendrier hégirien",
            sq = "Kalendari Hixhri", bs = "Hidžretski kalendar"
        ),
        "Sign In" to pack(
            ar = "تسجيل الدخول", ur = "سائن ان کریں", id = "Masuk", ms = "Log Masuk",
            hi = "साइन इन करें", tr = "Giriş Yap", de = "Anmelden", fr = "Se connecter",
            sq = "Hyni", bs = "Prijavite se"
        ),
        "Sign Out" to pack(
            ar = "تسجيل الخروج", ur = "سائن آؤٹ", id = "Keluar", ms = "Log Keluar",
            hi = "साइन आउट", tr = "Çıkış Yap", de = "Abmelden", fr = "Se déconnecter",
            sq = "Dilni", bs = "Odjavite se"
        ),
        "Language Settings Saved" to pack(
            ar = "تم حفظ إعدادات اللغة", ur = "زبان کی ترتیبات محفوظ ہو گئیں",
            id = "Pengaturan Bahasa Disimpan", ms = "Tetapan Bahasa Disimpan",
            hi = "भाषा सेटिंग्स सहेजी गईं", tr = "Dil Ayarları Kaydedildi",
            de = "Spracheinstellungen gespeichert", fr = "Paramètres de langue enregistrés",
            sq = "Cilësimet e gjuhës u ruajtën", bs = "Postavke jezika sačuvane"
        ),
        "Believer's Nasihah" to pack(
            ar = "نصيحة المؤمن اليومية", ur = "مومن کی یومیہ نصیحت",
            id = "Nasihat Mukmin Harian", ms = "Nasihat Mukmin Harian",
            hi = "मोमिन की दैनिक नसीहत", tr = "Müminin Günlük Nasihati",
            de = "Tägliche Ermahnung für Gläubige", fr = "Conseil quotidien du croyant",
            sq = "Këshilla ditore e besimtarit", bs = "Dnevni savjet vjernika"
        ),
        "AI Daily Wisdom" to pack(
            ar = "حكمة يومية بالذكاء الاصطناعي", ur = "اے آئی روزانہ حکمت",
            id = "Hikmah Harian AI", ms = "Hikmah Harian AI",
            hi = "एआई दैनिक हिकमत", tr = "Yapay Zeka Günlük Hikmet",
            de = "KI-Tagesweisheit", fr = "Sagesse quotidienne IA",
            sq = "Urtësia ditore AI", bs = "AI dnevna mudrost"
        ),
        "Generating today's AI Nasihah..." to pack(
            ar = "جارٍ إعداد نصيحة اليوم...", ur = "آج کی نصیحت تیار ہو رہی ہے...",
            id = "Menyusun nasihat AI hari ini...", ms = "Menjana nasihat AI hari ini...",
            hi = "आज की एआई नसीहत तैयार हो रही है...", tr = "Bugünün yapay zeka nasihati hazırlanıyor...",
            de = "Heutige KI-Ermahnung wird erstellt...", fr = "Génération du conseil IA du jour...",
            sq = "Duke përgatitur këshillën e sotme...", bs = "Priprema današnjeg AI savjeta..."
        ),
        "New Nasihah" to pack(
            ar = "نصيحة جديدة", ur = "نئی نصیحت",
            id = "Nasihat Baru", ms = "Nasihat Baharu",
            hi = "नई नसीहत", tr = "Yeni Nasihat",
            de = "Neuer Rat", fr = "Nouveau conseil",
            sq = "Këshillë e re", bs = "Novi savjet"
        ),
        "Practical Action:" to pack(
            ar = "العمل التطبيقي اليوم:", ur = "آج کا عملی اقدام:",
            id = "Amalan Praktis:", ms = "Amalan Praktikal:",
            hi = "आज का व्यावहारिक अमल:", tr = "Günlük Pratik Amel:",
            de = "Heutige Praxis:", fr = "Action pratique :",
            sq = "Veprimi praktik:", bs = "Praktično djelo:"
        ),
        "Retry" to pack(
            ar = "إعادة المحاولة", ur = "دوبارہ کوشش کریں",
            id = "Coba Lagi", ms = "Cuba Semula",
            hi = "पुनः प्रयास करें", tr = "Tekrar Dene",
            de = "Erneut versuchen", fr = "Réessayer",
            sq = "Provo përsëri", bs = "Pokušaj ponovo"
        ),
        "Virtues of Tahajjud?" to pack(
            ar = "ما فضل صلاة التهجد؟", ur = "تہجد کی فضیلت کیا ہے؟",
            id = "Keutamaan shalat Tahajud?", ms = "Kelebihan solat Tahajjud?",
            hi = "तहज्जुद की फ़ज़ीलत क्या है?", tr = "Teheccüd namazının fazileti nedir?",
            de = "Was sind die Tugenden des Tahajjud?", fr = "Quels sont les mérites du Tahajjud ?",
            sq = "Cilat janë mirësitë e namazit të Tehaxhudit?", bs = "Koje su odlike Tahaddžud-namaza?"
        ),
        "Who must pay Zakat?" to pack(
            ar = "على من تجب الزكاة؟", ur = "زکوٰۃ کس پر فرض ہے؟",
            id = "Siapa yang wajib membayar Zakat?", ms = "Siapakah yang wajib membayar Zakat?",
            hi = "ज़कात किस पर फ़र्ज़ है?", tr = "Zekât kimlere farzdır?",
            de = "Wer muss Zakat entrichten?", fr = "Qui doit payer la Zakat ?",
            sq = "Kush e ka obligim Zekatin?", bs = "Ko je dužan dati Zekat?"
        ),
        "Friday Sunnah deeds" to pack(
            ar = "سنن يوم الجمعة", ur = "جمعہ کے مسنون اعمال",
            id = "Amalan sunnah hari Jumat", ms = "Amalan sunat hari Jumaat",
            hi = "जुमा के सुन्नत आमाल", tr = "Cuma gününün sünnet amelleri",
            de = "Sunnah-Taten am Freitag", fr = "Actes sunnah du vendredi",
            sq = "Punët sunnet të ditës së xhuma", bs = "Sunneti dana džume"
        ),
        "Du'a for hardship" to pack(
            ar = "أدعية تفريج الهم والكرب", ur = "مصیبت اور پریشانی کی دعا",
            id = "Doa saat menghadapi kesulitan", ms = "Doa ketika ditimpa kesusahan",
            hi = "मुश्किल वक़्त की दुआ", tr = "Sıkıntı anında okunacak dua",
            de = "Bittgebet in schweren Zeiten", fr = "Doua en période d'épreuve",
            sq = "Dua në kohë vështirësie", bs = "Dova u teškim trenucima"
        ),
        "Change Profile Photo" to pack(
            ar = "تغيير صورة الملف الشخصي", ur = "پروفائل تصویر تبدیل کریں",
            id = "Ubah Foto Profil", ms = "Tukar Foto Profil",
            hi = "प्रोफ़ाइल फ़ोटो बदलें", tr = "Profil Fotoğrafını Değiştir",
            de = "Profilbild ändern", fr = "Changer la photo de profil",
            sq = "Ndrysho foton e profilit", bs = "Promijeni profilnu sliku"
        ),
        "Backup & Sync" to pack(
            ar = "النسخ الاحتياطي والمزامنة", ur = "بیک اپ اور ہم آہنگی",
            id = "Cadangkan & Sinkron", ms = "Sandar & Segerak",
            hi = "बैकअप और सिंक", tr = "Yedekle ve Eşitle",
            de = "Sichern & Synchronisieren", fr = "Sauvegarder & Synchroniser",
            sq = "Kopjo dhe sinkronizo", bs = "Sigurnosna kopija i sinhronizacija"
        ),
        "Restore from Cloud" to pack(
            ar = "استعادة من السحابة", ur = "کلاؤڈ سے بحال کریں",
            id = "Pulihkan dari Cloud", ms = "Pulihkan dari Awan",
            hi = "क्लाउड से पुनर्स्थापित करें", tr = "Buluttan Geri Yükle",
            de = "Aus der Cloud wiederherstellen", fr = "Restaurer depuis le cloud",
            sq = "Rikthe nga cloud", bs = "Vrati iz oblaka"
        ),
        "Transliteration:" to pack(
            ar = "النطق الميسر:", ur = "تلفظ:",
            id = "Transliterasi:", ms = "Transliterasi:",
            hi = "उच्चारण:", tr = "Okunuşu:",
            de = "Aussprache:", fr = "Phonétique :",
            sq = "Transliterimi:", bs = "Transliteracija:"
        ),
        "Pronunciation:" to pack(
            ar = "النطق:", ur = "تلفظ:",
            id = "Pelafalan:", ms = "Sebutan:",
            hi = "उच्चारण:", tr = "Telaffuz:",
            de = "Aussprache:", fr = "Prononciation :",
            sq = "Shqiptimi:", bs = "Izgovor:"
        ),
        "Translation:" to pack(
            ar = "المعنى:", ur = "ترجمہ:",
            id = "Terjemahan:", ms = "Terjemahan:",
            hi = "अनुवाद:", tr = "Meali:",
            de = "Übersetzung:", fr = "Traduction :",
            sq = "Përkthimi:", bs = "Prijevod:"
        ),
        "Meaning:" to pack(
            ar = "المعنى:", ur = "مفہوم:",
            id = "Arti:", ms = "Maksud:",
            hi = "अर्थ:", tr = "Anlamı:",
            de = "Bedeutung:", fr = "Signification :",
            sq = "Kuptimi:", bs = "Značenje:"
        ),
        "Reference:" to pack(
            ar = "المرجع:", ur = "حوالہ:",
            id = "Referensi:", ms = "Rujukan:",
            hi = "संदर्भ:", tr = "Kaynak:",
            de = "Quelle:", fr = "Référence :",
            sq = "Referenca:", bs = "Izvor:"
        ),
        "Brief Reflection:" to pack(
            ar = "تأمل وبيان:", ur = "مختصر تشریح:",
            id = "Renungan Singkat:", ms = "Huraian Ringkas:",
            hi = "संक्षिप्त व्याख्या:", tr = "Kısa Açıklama:",
            de = "Kurze Erläuterung:", fr = "Brève réflexion :",
            sq = "Shpjegim i shkurtër:", bs = "Kratko pojašnjenje:"
        ),
        "Lessons & Reflection:" to pack(
            ar = "الفوائد والتدبر:", ur = "دروس و حکمت:",
            id = "Pelajaran & Tadabbur:", ms = "Pengajaran & Tadabbur:",
            hi = "शिक्षा और चिंतन:", tr = "Dersler ve Tefekkür:",
            de = "Lehren & Besinnung:", fr = "Leçons et méditation :",
            sq = "Mësime dhe meditim:", bs = "Pouke i razmišljanje:"
        ),
        "Daily Prophetic Wisdom" to pack(
            ar = "حكمة نبوية يومية", ur = "روزانہ نبوی حکمت",
            id = "Hikmah Nabawi Harian", ms = "Hikmah Nabawi Harian",
            hi = "दैनिक नबवी हिकमत", tr = "Günlük Nebevi Hikmet",
            de = "Tägliche prophetische Weisheit", fr = "Sagesse prophétique du jour",
            sq = "Urtësi profetike ditore", bs = "Dnevna poslanička mudrost"
        ),
        "Read Explanation" to pack(
            ar = "اقرأ الشرح", ur = "تشریح پڑھیں",
            id = "Baca Penjelasan", ms = "Baca Huraian",
            hi = "व्याख्या पढ़ें", tr = "Açıklamayı Oku",
            de = "Erklärung lesen", fr = "Lire l'explication",
            sq = "Lexo shpjegimin", bs = "Pročitaj objašnjenje"
        ),
        "Listen" to pack(
            ar = "استمع", ur = "سنیں",
            id = "Dengarkan", ms = "Dengar",
            hi = "सुनें", tr = "Dinle",
            de = "Anhören", fr = "Écouter",
            sq = "Dëgjo", bs = "Slušaj"
        ),
        "Previous" to pack(
            ar = "السابق", ur = "پچھلا",
            id = "Sebelumnya", ms = "Sebelumnya",
            hi = "पिछला", tr = "Önceki",
            de = "Zurück", fr = "Précédent",
            sq = "Paraardhës", bs = "Prethodno"
        ),
        "Next" to pack(
            ar = "التالي", ur = "اگلا",
            id = "Berikutnya", ms = "Seterusnya",
            hi = "अगला", tr = "Sonraki",
            de = "Weiter", fr = "Suivant",
            sq = "Tjetër", bs = "Sljedeće"
        ),
        "Current GPS Location" to pack(
            ar = "الموقع الجغرافي الحالي", ur = "موجودہ جی پی ایس مقام",
            id = "Lokasi GPS Saat Ini", ms = "Lokasi GPS Semasa",
            hi = "वर्तमान जीपीएस स्थान", tr = "Mevcut GPS Konumu",
            de = "Aktueller GPS-Standort", fr = "Position GPS actuelle",
            sq = "Vendndodhja aktuale GPS", bs = "Trenutna GPS lokacija"
        ),
        "Detecting GPS..." to pack(
            ar = "جارٍ تحديد الموقع...", ur = "مقام تلاش کیا جا رہا ہے...",
            id = "Mendeteksi GPS...", ms = "Mengesan GPS...",
            hi = "जीपीएस खोजा जा रहा है...", tr = "GPS Algılanıyor...",
            de = "GPS wird ermittelt...", fr = "Détection GPS...",
            sq = "Duke zbuluar GPS...", bs = "Otkrivanje GPS-a..."
        ),
        "Auto-Detect GPS Location" to pack(
            ar = "تحديد الموقع تلقائيًا عبر GPS", ur = "جی پی ایس سے خودکار مقام معلوم کریں",
            id = "Deteksi Otomatis Lokasi GPS", ms = "Kesan Lokasi GPS Automatik",
            hi = "जीपीएस स्थान स्वतः पहचानें", tr = "GPS Konumunu Otomatik Algıla",
            de = "GPS-Standort automatisch erkennen", fr = "Détecter la position GPS",
            sq = "Zbulo automatikisht vendndodhjen GPS", bs = "Automatski otkrij GPS lokaciju"
        ),
        "Bangladesh" to pack(
            ar = "بنغلاديش", ur = "بنگلہ دیش",
            id = "Bangladesh", ms = "Bangladesh",
            hi = "बांग्लादेश", tr = "Bangladeş",
            de = "Bangladesch", fr = "Bangladesh",
            sq = "Bangladeshi", bs = "Bangladeš"
        ),
        "Makkah" to pack(
            ar = "مكة المكرمة", ur = "مکہ مکرمہ",
            id = "Mekah", ms = "Makkah",
            hi = "मक्का मुकर्रमा", tr = "Mekke",
            de = "Mekka", fr = "La Mecque",
            sq = "Mekë", bs = "Meka"
        ),
        "Madinah" to pack(
            ar = "المدينة المنورة", ur = "مدینہ منورہ",
            id = "Madinah", ms = "Madinah",
            hi = "मदीना मुनव्वरा", tr = "Medine",
            de = "Medina", fr = "Médine",
            sq = "Medinë", bs = "Medina"
        ),
        "Current:" to pack(
            ar = "الحالي:", ur = "موجودہ:",
            id = "Saat ini:", ms = "Semasa:",
            hi = "वर्तमान:", tr = "Mevcut:",
            de = "Aktuell:", fr = "Actuel :",
            sq = "Aktualisht:", bs = "Trenutno:"
        ),
        "Root Letters" to pack(
            ar = "الجذر اللغوي", ur = "مادہ / اصل حروف",
            id = "Akar Kata", ms = "Kata Akar",
            hi = "मूल अक्षर", tr = "Kök Harfler",
            de = "Wortwurzel", fr = "Racine du mot",
            sq = "Rrënja e fjalës", bs = "Korijen riječi"
        ),
        "Grammar Form" to pack(
            ar = "التحليل النحوي", ur = "نحوی حیثیت",
            id = "Bentuk Tata Bahasa", ms = "Bentuk Tatabahasa",
            hi = "व्याकरण रूप", tr = "Dilbilgisi Yapısı",
            de = "Grammatische Form", fr = "Forme grammaticale",
            sq = "Forma gramatikore", bs = "Gramatički oblik"
        ),
        "Full Ayah Translation:" to pack(
            ar = "معنى الآية الكاملة:", ur = "مکمل آیت کا ترجمہ:",
            id = "Terjemahan Ayat Lengkap:", ms = "Terjemahan Ayat Penuh:",
            hi = "संपूर्ण आयत का अनुवाद:", tr = "Tam Ayet Meali:",
            de = "Vollständige Versübersetzung:", fr = "Traduction complète du verset :",
            sq = "Përkthimi i plotë i ajetit:", bs = "Kompletan prijevod ajeta:"
        )
    )

    private val lowercaseLookup: Map<String, LocalizedPack> by lazy {
        entries.entries.associate { (k, v) -> k.trim().lowercase() to v }
    }

    /**
     * Translates a UI or content string cleanly across all 12 supported languages.
     * - If target language is `bn`: returns `bn`.
     * - If target language is `en`: returns `en`.
     * - Otherwise: checks curated translation table and falls back cleanly to English (`en`), NEVER Bengali!
     */
    fun translate(langCode: String, bn: String, en: String): String {
        val lang = SupportedLanguage.fromCode(langCode)
        return when (lang) {
            SupportedLanguage.BENGALI -> bn
            SupportedLanguage.ENGLISH -> en
            else -> {
                val cleanEn = en.trim()
                val direct = entries[cleanEn]?.forLang(lang)
                    ?: lowercaseLookup[cleanEn.lowercase()]?.forLang(lang)
                if (!direct.isNullOrBlank()) return direct

                translatePattern(lang, cleanEn) ?: en
            }
        }
    }

    private fun translatePattern(lang: SupportedLanguage, en: String): String? {
        // Common numeric prefixes/suffixes so counters & chapter badges localize across all languages
        Regex("""^Page (\d+)$""", RegexOption.IGNORE_CASE).matchEntire(en)?.let { m ->
            val p = m.groupValues[1]
            return when (lang) {
                SupportedLanguage.ARABIC -> "صفحة $p"
                SupportedLanguage.URDU -> "صفحہ $p"
                SupportedLanguage.INDONESIAN -> "Halaman $p"
                SupportedLanguage.MALAY -> "Halaman $p"
                SupportedLanguage.HINDI -> "पृष्ठ $p"
                SupportedLanguage.TURKISH -> "Sayfa $p"
                SupportedLanguage.GERMAN -> "Seite $p"
                SupportedLanguage.FRENCH -> "Page $p"
                SupportedLanguage.ALBANIAN -> "Faqja $p"
                SupportedLanguage.BOSNIAN -> "Stranica $p"
                else -> en
            }
        }
        Regex("""^(\d+) Chapters$""", RegexOption.IGNORE_CASE).matchEntire(en)?.let { m ->
            val n = m.groupValues[1]
            return when (lang) {
                SupportedLanguage.ARABIC -> "$n أبواب"
                SupportedLanguage.URDU -> "$n ابواب"
                SupportedLanguage.INDONESIAN -> "$n Bab"
                SupportedLanguage.MALAY -> "$n Bab"
                SupportedLanguage.HINDI -> "$n अध्याय"
                SupportedLanguage.TURKISH -> "$n Bölüm"
                SupportedLanguage.GERMAN -> "$n Kapitel"
                SupportedLanguage.FRENCH -> "$n Chapitres"
                SupportedLanguage.ALBANIAN -> "$n Kapituj"
                SupportedLanguage.BOSNIAN -> "$n Poglavlja"
                else -> en
            }
        }
        return null
    }
}
