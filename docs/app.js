(() => {
  "use strict";

  const REPO_OWNER = "stupidboytanvir-droid";
  const REPO_NAME = "Noor-E-Hidaya";
  const GITHUB_LATEST_API = `https://api.github.com/repos/${REPO_OWNER}/${REPO_NAME}/releases/latest`;
  const GITHUB_RELEASES_FALLBACK_URL = `https://github.com/${REPO_OWNER}/${REPO_NAME}/releases/latest`;
  const LOCAL_FALLBACK_JSON = "./latest-release.json";
  const STORAGE_LANG_KEY = "noor_hidayah_site_lang";

  const I18N = {
    en: {
      navFeatures: "Features",
      navWhatsNew: "What's New",
      navInstall: "Install Guide",
      navFaq: "FAQ",
      navDownload: "Download APK",
      heroBadge: "Official Android Release • Free & Direct",
      heroTitle: "Noor-e-Hidayah — নূর-ই-হিদায়াহ",
      heroTagline:
        "Your complete, globally accurate Islamic companion for Android — uniting Mosque Mode auto-silent & auto-SMS reply, international Qibla compass, worldwide prayer times, Al-Quran, Du'a, Ruqyah, Daily Hadith, AI Nasihah, and a 41-token Custom Theme Studio.",
      metaVersionLabel: "Version",
      metaSizeLabel: "APK Size",
      metaDateLabel: "Released",
      metaAndroid: "Android 7.0+",
      ctaDownload: "Download Latest APK",
      ctaGitHub: "View on GitHub Releases",
      syncChecking: "Checking live GitHub Release…",
      syncLive: "Live synced with GitHub Releases",
      syncFallback: "Synced via cached release metadata",
      syncFallbackDirect: "Direct GitHub Releases link active",
      featuresKicker: "Crafted for Daily Worship",
      featuresTitle: "Everything You Need in One Peaceful Islamic App",
      featuresSubtitle:
        "Thoughtfully engineered for Muslims in Bangladesh and worldwide — blending pinpoint accuracy, quiet reverence, and deep personalization.",
      featBadgeFlagship: "Signature",
      featBadgeUpgraded: "Upgraded",
      featBadgeNew: "New",
      feat1Title: "Mosque Mode (Auto-Silent & Auto-SMS)",
      feat1Desc:
        "Automatically silences or vibrates your phone for every Jama'at, notifies you 15 minutes before prayer begins, and optionally sends a polite Auto-SMS reply to missed callers while you are in Salah.",
      feat2Title: "International Qibla Compass",
      feat2Desc:
        "Strictly upright center Kaaba medallion with one-tap Flexible Pointer and Classic Rotating Dial modes, real-time spherical great-circle azimuth, True North magnetic declination, and ±3° green alignment lock.",
      feat3Title: "Global Time Zone & Accurate Prayer Times",
      feat3Desc:
        "Auto-GPS or manual global city & time zone binding with live Waqt countdowns, Sunrise/Ishraq transitions, prohibited Makruh time alerts, and 12h/24h time format synced cleanly across Mosque Mode.",
      feat4Title: "Al-Quran, Du'a, Ruqyah, Hadith & AI Nasihah",
      feat4Desc:
        "Read and listen to Al-Quran in the Tilawat Studio, explore authentic Du'a & Ruqyah with audio playback, reflect on Daily Hadith, and receive uplifting, authentic Islamic AI Nasihah anytime.",
      feat5Title: 'Dynamic Currency Loan ("হাওলাত") Tracker',
      feat5Desc:
        "Keep transparent, sunnah-aligned Qard Hasan & personal lending records with automatic location-based currency adaptation (৳ BDT, € EUR, $ USD, ﷼ SAR, and more).",
      feat6Title: "Ultra-Granular Custom Theme Studio",
      feat6Desc:
        "Personalize 41 independent color tokens across every card, text category, border, navigation dock, and Islamic watermark — complete with Live Preview, RGB/HEX picker, and cloud-synced Theme Profiles.",
      feat7Title: "True In-App Self-Updater & Secure Cloud Backup",
      feat7Desc:
        "No external browser juggling required: Noor-e-Hidayah checks official GitHub Releases inside the app, downloads the verified APK with a live progress bar, and launches the native Android installer directly — while safely syncing your settings, worship logs, and theme profiles via Google Sign-In.",
      changelogKicker: "Auto-Synced from GitHub Releases",
      changelogTitle: "What's New in the Latest Release",
      changelogSubtitle:
        "Release notes, file size, and download links stay automatically synchronized whenever a new version is published.",
      changelogLoading: "Loading latest release notes…",
      changelogDownloadBtn: "Download This Release APK",
      changelogAllReleases: "Browse all previous versions on GitHub →",
      installKicker: "Quick Setup",
      installTitle: "How to Install Noor-e-Hidayah APK in 3 Easy Steps",
      installSubtitle:
        "Direct installation takes less than a minute on any Android 7.0+ smartphone.",
      step1Title: "Download the Official APK",
      step1Desc:
        'Tap the golden "Download Latest APK" button above. Your browser will download the verified APK file directly from our official GitHub Releases server.',
      step2Title: 'Allow "Install Unknown Apps" Safely',
      step2Desc:
        'When prompted by Android, tap Settings and enable "Allow from this source" for Chrome or your File Manager. This standard Android permission is required once for apps installed outside the Play Store.',
      step3Title: "Install & Stay Auto-Updated",
      step3Desc:
        'Tap "Install", open Noor-e-Hidayah, and grant optional location/notification permissions. Future updates can be installed with one tap inside More → Check for Updates.',
      trustNote:
        "100% Official & Transparent Hosting: Every APK served on this page comes straight from github.com/stupidboytanvir-droid/Noor-E-Hidaya/releases — never re-uploaded to mirror sites, never bundled with adware.",
      faqKicker: "Questions & Answers",
      faqTitle: "Frequently Asked Questions",
      faq1Q: "Is the Noor-e-Hidayah APK safe and directly from the developer?",
      faq1A:
        "Yes. All download buttons automatically point directly to official GitHub Releases hosted under stupidboytanvir-droid/Noor-E-Hidaya. There are no third-party APK mirrors or modified packages.",
      faq2Q: "Do I need to revisit this page to get future updates?",
      faq2A:
        "No — Noor-e-Hidayah features a true In-App Self-Updater inside More → Check for Updates. However, if you ever share the link with friends or switch phones, this page always provides the newest APK automatically.",
      faq3Q: "Does Mosque Mode and Qibla Compass work accurately outside Bangladesh?",
      faq3A:
        "Yes! Whether you are in Bangladesh, Europe, North America, or the Middle East, prayer timings, Mosque Mode silent triggers, dynamic currency, and spherical great-circle Qibla bearing with True North correction automatically sync with your active GPS or chosen global time zone.",
      faq4Q: "Does the app support both Bangla and English?",
      faq4A:
        "Yes. Noor-e-Hidayah is fully bilingual across Bangla and English, including Arabic script, Bangla/English translations, transliteration, audio recitations, and customizable typography & themes.",
      faq5Q: "Is Noor-e-Hidayah completely free to use?",
      faq5A:
        "Yes, Noor-e-Hidayah is 100% free with no distracting banner ads during your Quran recitation, Du'a, or prayer preparation.",
      footerDownload: "Download Latest APK",
      footerReleases: "All Releases",
      footerRepo: "GitHub Repository",
      footerSupport: "Report an Issue",
      footerCopy:
        "© 2026 Noor-e-Hidayah (নূর-ই-হিদায়াহ). Official Android APK hosted transparently on GitHub Releases."
    },
    bn: {
      navFeatures: "ফিচারসমূহ",
      navWhatsNew: "নতুন আপডেট",
      navInstall: "ইনস্টল গাইড",
      navFaq: "জিজ্ঞাসা",
      navDownload: "APK ডাউনলোড",
      heroBadge: "অফিশিয়াল অ্যান্ড্রয়েড রিলিজ • সম্পূর্ণ ফ্রি",
      heroTitle: "নূর-ই-হিদায়াহ — Noor-e-Hidayah",
      heroTagline:
        "বাংলাদেশ ও বিশ্বব্যাপী মুসলিমদের জন্য পরিপূর্ণ ইসলামিক অ্যান্ড্রয়েড অ্যাপ — মসজিদ মোড (অটো-সাইলেন্ট ও অটো-এসএমএস), আন্তর্জাতিক কিবলা কম্পাস, সঠিক নামাজের সময়, আল-কুরআন, দু'আ, রুকইয়াহ, দৈনিক হাদিস, এআই নসিহত এবং ৪১-টোকেনের কাস্টম থিম স্টুডিও একসাথে।",
      metaVersionLabel: "ভার্সন",
      metaSizeLabel: "ফাইল সাইজ",
      metaDateLabel: "প্রকাশের তারিখ",
      metaAndroid: "অ্যান্ড্রয়েড ৭.০+",
      ctaDownload: "সর্বশেষ APK ডাউনলোড করুন",
      ctaGitHub: "GitHub Releases দেখুন",
      syncChecking: "লাইভ GitHub রিলিজ যাচাই করা হচ্ছে…",
      syncLive: "GitHub Releases থেকে লাইভ সিঙ্কড",
      syncFallback: "ক্যাশড রিলিজ তথ্য থেকে লোড হয়েছে",
      syncFallbackDirect: "সরাসরি GitHub Releases লিংক সক্রিয়",
      featuresKicker: "দৈনন্দিন ইবাদতের নির্ভরযোগ্য সঙ্গী",
      featuresTitle: "একটি শান্তিময় ইসলামিক অ্যাপে প্রয়োজনীয় সবকিছু",
      featuresSubtitle:
        "বাংলাদেশসহ বিশ্বের যেকোনো প্রান্তে নিখুঁত সময়, নীরব মসজিদ পরিবেশ এবং নিজের পছন্দমতো ডিজাইনের জন্য যত্নসহকারে তৈরি।",
      featBadgeFlagship: "বিশেষ ফিচার",
      featBadgeUpgraded: "আপগ্রেডেড",
      featBadgeNew: "নতুন",
      feat1Title: "মসজিদ মোড (অটো-সাইলেন্ট ও অটো-এসএমএস)",
      feat1Desc:
        "প্রতিটি জামায়াতের সময় ফোন স্বয়ংক্রিয়ভাবে সাইলেন্ট বা ভাইব্রেশন মোডে চলে যায়, জামায়াতের ১৫ মিনিট আগে রিমাইন্ডার দেয় এবং নামাজের সময় মিসড কলের উত্তরে ভদ্র অটো-এসএমএস পাঠায়।",
      feat2Title: "আন্তর্জাতিক মানের কিবলা কম্পাস",
      feat2Desc:
        "কেন্দ্রে সোজা কা'বা আইকন, এক ট্যাপে ফ্লেক্সিবল পয়েন্টার ও ক্লাসিক রোটেটিং ডায়াল মোড, বিশ্বব্যাপী গ্রেট-সার্কেল ডাইনামিক বিয়ারিং, True North কারেকশন এবং ±৩° গ্রিন লক ইন্ডিকেটর।",
      feat3Title: "গ্লোবাল টাইম জোন ও নিখুঁত নামাজের সময়সূচী",
      feat3Desc:
        "অটো-জিপিএস বা ম্যানুয়াল বিশ্বব্যাপী শহর ও টাইম জোন সিঙ্ক, লাইভ ওয়াক্ত কাউন্টডাউন, সূর্যোদয়/ইশরাক ট্রানজিশন, মাকরুহ ওয়াক্ত সতর্কতা এবং ১২/২৪ ঘণ্টা ফরম্যাট।",
      feat4Title: "আল-কুরআন, দু'আ, রুকইয়াহ, হাদিস ও এআই নসিহত",
      feat4Desc:
        "তেলাওয়াত স্টুডিওতে অডিওসহ আল-কুরআন, সহীহ দু'আ ও রুকইয়াহ অডিও প্লেব্যাক, দৈনিক হাদিস এবং যেকোনো সময় ইসলামিক এআই নসিহত পড়ার সুবিধা।",
      feat5Title: "ডাইনামিক কারেন্সি হাওলাত (ঋণ) ট্র্যাকার",
      feat5Desc:
        "সুন্নাহসম্মত কর্জে হাসানা ও ব্যক্তিগত লেনদেনের স্বচ্ছ হিসাব রাখুন — আপনার লোকেশন অনুযায়ী স্বয়ংক্রিয় মুদ্রা (৳ BDT, € EUR, $ USD, ﷼ SAR ইত্যাদি) সমর্থনসহ।",
      feat6Title: "আল্ট্রা-গ্র্যানুলার কাস্টম থিম স্টুডিও",
      feat6Desc:
        "অ্যাপের প্রতিটি কার্ড, টেক্সট, বর্ডার, নেভিগেশন বার ও ইসলামিক ওয়াটারমার্কের ৪১টি আলাদা রঙ পরিবর্তন করুন — লাইভ প্রিভিউ, RGB/HEX পিকার এবং ক্লাউড-সিঙ্কড থিম প্রোফাইলসহ।",
      feat7Title: "অ্যাপের ভেতরেই সেলফ-আপডেটার ও ক্লাউড ব্যাকআপ",
      feat7Desc:
        "আলাদা ব্রাউজারে যাওয়ার ঝামেলা নেই: 'আরও → আপডেট চেক করুন' থেকে সরাসরি অফিশিয়াল GitHub APK ডাউনলোড ও ইনস্টল করুন এবং Google Sign-In এর মাধ্যমে আপনার সব ডেটা নিরাপদে সংরক্ষণ করুন।",
      changelogKicker: "GitHub Releases থেকে স্বয়ংক্রিয়ভাবে সিঙ্কড",
      changelogTitle: "সর্বশেষ ভার্সনে যা নতুন যুক্ত হয়েছে",
      changelogSubtitle:
        "নতুন ভার্সন রিলিজ হলেই রিলিজ নোট, APK সাইজ এবং ডাউনলোড লিংক এই পেজে স্বয়ংক্রিয়ভাবে আপডেট হয়ে যায়।",
      changelogLoading: "সর্বশেষ রিলিজ নোট লোড হচ্ছে…",
      changelogDownloadBtn: "এই ভার্সনের APK ডাউনলোড করুন",
      changelogAllReleases: "GitHub-এ পূর্ববর্তী সব ভার্সন দেখুন →",
      installKicker: "সহজ ইনস্টলেশন",
      installTitle: "মাত্র ৩ ধাপে নূর-ই-হিদায়াহ APK ইনস্টল করুন",
      installSubtitle:
        "যেকোনো অ্যান্ড্রয়েড ৭.০ বা পরবর্তী ফোনে ১ মিনিটেরও কম সময়ে নিরাপদে ইনস্টল করুন।",
      step1Title: "অফিশিয়াল APK ডাউনলোড করুন",
      step1Desc:
        'উপরের সোনালি "সর্বশেষ APK ডাউনলোড করুন" বাটনে ট্যাপ করুন। আপনার ব্রাউজার সরাসরি আমাদের অফিশিয়াল GitHub Releases সার্ভার থেকে APK ফাইলটি ডাউনলোড করবে।',
      step2Title: '"Install unknown apps" অনুমতি দিন',
      step2Desc:
        'অ্যান্ড্রয়েড পপ-আপ দেখালে Settings-এ ট্যাপ করে Chrome বা আপনার File Manager-এর জন্য "Allow from this source" চালু করুন। প্লে-স্টোরের বাইরে থেকে অ্যাপ ইনস্টল করতে এটি একবারই প্রয়োজন হয়।',
      step3Title: "ইনস্টল করুন ও অটো-আপডেট পান",
      step3Desc:
        '"Install" ট্যাপ করে অ্যাপটি ওপেন করুন। ভবিষ্যতের সব নতুন আপডেট অ্যাপের ভেতরেই "আরও → আপডেট চেক করুন" থেকে এক ট্যাপে ইনস্টল করতে পারবেন।',
      trustNote:
        "১০০% অফিশিয়াল ও নিরাপদ হোস্টিং: এই পেজের প্রতিটি APK সরাসরি github.com/stupidboytanvir-droid/Noor-E-Hidaya/releases থেকে আসে — কোনো থার্ড-পার্টি মিরর বা ক্ষতিকর বিজ্ঞাপন নেই।",
      faqKicker: "সচরাচর জিজ্ঞাসা",
      faqTitle: "সাধারণ প্রশ্ন ও উত্তর",
      faq1Q: "নূর-ই-হিদায়াহ APK ফাইলটি কি সম্পূর্ণ নিরাপদ ও অফিশিয়াল?",
      faq1A:
        "জি, হ্যাঁ। এই পেজের প্রতিটি ডাউনলোড লিংক সরাসরি ডেভেলপারের অফিশিয়াল GitHub Releases (stupidboytanvir-droid/Noor-E-Hidaya) থেকে সংযুক্ত। কোনো থার্ড-পার্টি মিরর বা পরিবর্তিত ফাইল ব্যবহার করা হয় না।",
      faq2Q: "পরবর্তী আপডেট পেতে কি প্রতিবার এই ওয়েবসাইটে আসতে হবে?",
      faq2A:
        "না — অ্যাপের ভেতরেই 'আরও → আপডেট চেক করুন' নামে ইন-অ্যাপ সেলফ-আপডেটার রয়েছে। তবে নতুন ফোনে ইনস্টল করতে বা অন্যদের সাথে শেয়ার করতে এই পেজ সবসময় সর্বশেষ APK দেবে।",
      faq3Q: "বাংলাদেশের বাইরে থেকেও কি মসজিদ মোড ও কিবলা কম্পাস সঠিকভাবে কাজ করে?",
      faq3A:
        "জি! বাংলাদেশ, ইউরোপ, আমেরিকা বা মধ্যপ্রাচ্য — বিশ্বের যেকোনো জায়গায় আপনার জিপিএস বা নির্বাচিত টাইম জোন অনুযায়ী নামাজের সময়, মসজিদ মোড, কারেন্সি এবং কিবলা বিয়ারিং স্বয়ংক্রিয়ভাবে নিখুঁত কাজ করে।",
      faq4Q: "অ্যাপটিতে কি বাংলা এবং ইংরেজি দুই ভাষাই আছে?",
      faq4A:
        "হ্যাঁ, পুরো অ্যাপে বাংলা ও ইংরেজি দুই ভাষার সম্পূর্ণ সাপোর্ট রয়েছে — আরবি আয়াত, বাংলা/ইংরেজি অনুবাদ, উচ্চারণ এবং অডিও তেলাওয়াতসহ।",
      faq5Q: "নূর-ই-হিদায়াহ কি সম্পূর্ণ ফ্রি?",
      faq5A:
        "জি, নূর-ই-হিদায়াহ ১০০% ফ্রি এবং কুরআন তেলাওয়াত, দু'আ বা নামাজের সময় কোনো বিরক্তিকর বিজ্ঞাপন দেখানো হয় না।",
      footerDownload: "সর্বশেষ APK ডাউনলোড",
      footerReleases: "সব রিলিজ দেখুন",
      footerRepo: "GitHub রিপোজিটরি",
      footerSupport: "সমস্যা জানান",
      footerCopy:
        "© ২০২৬ নূর-ই-হিদায়াহ (Noor-e-Hidayah)। অফিশিয়াল অ্যান্ড্রয়েড APK সরাসরি GitHub Releases-এ হোস্টেড।"
    }
  };

  let currentLang = detectInitialLang();
  let currentRelease = null;
  let currentSyncMode = "checking";

  function detectInitialLang() {
    try {
      const saved = localStorage.getItem(STORAGE_LANG_KEY);
      if (saved === "en" || saved === "bn") return saved;
    } catch (_) {
      // Ignore storage access errors in private browsing
    }
    const navLang = (navigator.language || "").toLowerCase();
    return navLang.startsWith("bn") ? "bn" : "en";
  }

  function formatMegabytes(bytes) {
    if (typeof bytes !== "number" || !Number.isFinite(bytes) || bytes <= 0) {
      return null;
    }
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  function formatReleaseDate(isoString, lang) {
    if (!isoString) return "—";
    const date = new Date(isoString);
    if (Number.isNaN(date.getTime())) return "—";
    try {
      return new Intl.DateTimeFormat(lang === "bn" ? "bn-BD" : "en-US", {
        year: "numeric",
        month: "short",
        day: "numeric"
      }).format(date);
    } catch (_) {
      return isoString.slice(0, 10);
    }
  }

  function appendFormattedInline(parent, text) {
    // Safely parse **bold** and `code` tokens using pure DOM text nodes (no raw innerHTML)
    const tokenRegex = /(\*\*[^*]+\*\*|`[^`]+`)/g;
    let lastIndex = 0;
    let match;

    while ((match = tokenRegex.exec(text)) !== null) {
      if (match.index > lastIndex) {
        parent.appendChild(document.createTextNode(text.slice(lastIndex, match.index)));
      }
      const token = match[0];
      if (token.startsWith("**") && token.endsWith("**")) {
        const strong = document.createElement("strong");
        strong.textContent = token.slice(2, -2);
        parent.appendChild(strong);
      } else if (token.startsWith("`") && token.endsWith("`")) {
        const code = document.createElement("code");
        code.textContent = token.slice(1, -1);
        parent.appendChild(code);
      }
      lastIndex = tokenRegex.lastIndex;
    }

    if (lastIndex < text.length) {
      parent.appendChild(document.createTextNode(text.slice(lastIndex)));
    }
  }

  function renderReleaseNotesSafely(container, markdownBody) {
    container.textContent = "";
    const text = (markdownBody || "").trim();
    if (!text) {
      const p = document.createElement("p");
      p.textContent =
        currentLang === "bn"
          ? "সর্বশেষ ভার্সনের বিস্তারিত পরিবর্তন দেখতে GitHub Releases পেজ দেখুন।"
          : "View the official GitHub Release page for full details of this update.";
      container.appendChild(p);
      return;
    }

    const lines = text.split(/\r?\n/);
    let currentList = null;

    const flushList = () => {
      if (currentList) {
        container.appendChild(currentList);
        currentList = null;
      }
    };

    for (const rawLine of lines) {
      const line = rawLine.trim();
      if (!line || /^---+$/.test(line)) {
        flushList();
        continue;
      }

      if (/^#{1,4}\s+/.test(line)) {
        flushList();
        const headingText = line.replace(/^#{1,4}\s+/, "").trim();
        const h = document.createElement(line.startsWith("###") ? "h4" : "h3");
        appendFormattedInline(h, headingText);
        container.appendChild(h);
        continue;
      }

      if (/^[-*•]\s+/.test(line)) {
        if (!currentList) {
          currentList = document.createElement("ul");
        }
        const li = document.createElement("li");
        appendFormattedInline(li, line.replace(/^[-*•]\s+/, "").trim());
        currentList.appendChild(li);
        continue;
      }

      flushList();
      const p = document.createElement("p");
      appendFormattedInline(p, line);
      container.appendChild(p);
    }

    flushList();
  }

  function updateSoftwareJsonLd(releaseInfo) {
    const scriptEl = document.getElementById("ld-software-app");
    if (!scriptEl) return;
    try {
      const schema = JSON.parse(scriptEl.textContent || "{}");
      if (releaseInfo.tagName) {
        schema.softwareVersion = releaseInfo.tagName.replace(/^v/i, "");
      }
      if (releaseInfo.downloadUrl) {
        schema.downloadUrl = releaseInfo.downloadUrl;
      }
      if (releaseInfo.sizeLabel) {
        schema.fileSize = releaseInfo.sizeLabel;
      }
      if (releaseInfo.publishedAt) {
        schema.datePublished = releaseInfo.publishedAt.slice(0, 10);
      }
      scriptEl.textContent = JSON.stringify(schema, null, 2);
    } catch (_) {
      // Keep static JSON-LD intact if parsing fails
    }
  }

  function extractReleaseInfo(data) {
    if (!data || typeof data !== "object") return null;
    const assets = Array.isArray(data.assets) ? data.assets : [];
    const apkAsset = assets.find(
      (asset) =>
        asset &&
        typeof asset.name === "string" &&
        asset.name.toLowerCase().endsWith(".apk") &&
        typeof asset.browser_download_url === "string"
    );

    const tagName = typeof data.tag_name === "string" && data.tag_name.trim() ? data.tag_name.trim() : "v1.2.0";
    const releaseName = typeof data.name === "string" && data.name.trim() ? data.name.trim() : `Version ${tagName.replace(/^v/i, "")}`;
    const publishedAt = typeof data.published_at === "string" ? data.published_at : "";
    const htmlUrl =
      typeof data.html_url === "string" && data.html_url.startsWith("https://")
        ? data.html_url
        : GITHUB_RELEASES_FALLBACK_URL;

    return {
      tagName,
      releaseName,
      publishedAt,
      htmlUrl,
      body: typeof data.body === "string" ? data.body : "",
      apkName: apkAsset ? apkAsset.name : `Noor-e-Hidayah-${tagName}.apk`,
      sizeLabel: apkAsset ? formatMegabytes(apkAsset.size) : null,
      downloadUrl: apkAsset ? apkAsset.browser_download_url : htmlUrl
    };
  }

  function applyReleaseToUI(info) {
    if (!info) return;
    currentRelease = info;

    const versionEl = document.getElementById("release-version");
    const ctaTagEl = document.getElementById("cta-version-tag");
    const sizeEl = document.getElementById("release-size");
    const dateEl = document.getElementById("release-date");
    const apkNameEl = document.getElementById("apk-filename");
    const githubLinkEl = document.getElementById("github-release-link");

    const changelogTagEl = document.getElementById("changelog-tag");
    const changelogNameEl = document.getElementById("changelog-release-name");
    const changelogDateEl = document.getElementById("changelog-date");
    const changelogBodyEl = document.getElementById("changelog-body");

    const formattedDate = formatReleaseDate(info.publishedAt, currentLang);

    if (versionEl) versionEl.textContent = info.tagName;
    if (ctaTagEl) ctaTagEl.textContent = `(${info.tagName})`;
    if (sizeEl && info.sizeLabel) sizeEl.textContent = info.sizeLabel;
    if (dateEl) dateEl.textContent = formattedDate;
    if (apkNameEl) apkNameEl.textContent = info.apkName;
    if (githubLinkEl) githubLinkEl.href = info.htmlUrl;

    document.querySelectorAll(".js-apk-download").forEach((link) => {
      link.setAttribute("href", info.downloadUrl);
      if (info.apkName && info.downloadUrl.toLowerCase().endsWith(".apk")) {
        link.setAttribute("download", info.apkName);
      }
    });

    if (changelogTagEl) changelogTagEl.textContent = info.tagName;
    if (changelogNameEl) changelogNameEl.textContent = info.releaseName;
    if (changelogDateEl) {
      changelogDateEl.textContent = formattedDate;
      if (info.publishedAt) {
        changelogDateEl.setAttribute("datetime", info.publishedAt.slice(0, 10));
      }
    }
    if (changelogBodyEl) {
      renderReleaseNotesSafely(changelogBodyEl, info.body);
    }

    updateSoftwareJsonLd(info);
  }

  function updateSyncStatusBadge() {
    const badgeEl = document.getElementById("sync-status-badge");
    if (!badgeEl) return;
    const dict = I18N[currentLang] || I18N.en;
    if (currentSyncMode === "live") {
      badgeEl.textContent = dict.syncLive;
    } else if (currentSyncMode === "fallback") {
      badgeEl.textContent = dict.syncFallback;
    } else if (currentSyncMode === "direct") {
      badgeEl.textContent = dict.syncFallbackDirect;
    } else {
      badgeEl.textContent = dict.syncChecking;
    }
  }

  function applyLanguage(lang) {
    currentLang = lang === "bn" ? "bn" : "en";
    document.documentElement.lang = currentLang;

    const dict = I18N[currentLang] || I18N.en;
    document.querySelectorAll("[data-i18n]").forEach((el) => {
      const key = el.getAttribute("data-i18n");
      if (key && Object.prototype.hasOwnProperty.call(dict, key)) {
        el.textContent = dict[key];
      }
    });

    const btnEn = document.getElementById("btn-lang-en");
    const btnBn = document.getElementById("btn-lang-bn");
    if (btnEn && btnBn) {
      const isBn = currentLang === "bn";
      btnEn.classList.toggle("is-active", !isBn);
      btnEn.setAttribute("aria-pressed", String(!isBn));
      btnBn.classList.toggle("is-active", isBn);
      btnBn.setAttribute("aria-pressed", String(isBn));
    }

    if (currentRelease) {
      const formattedDate = formatReleaseDate(currentRelease.publishedAt, currentLang);
      const dateEl = document.getElementById("release-date");
      const changelogDateEl = document.getElementById("changelog-date");
      if (dateEl) dateEl.textContent = formattedDate;
      if (changelogDateEl) changelogDateEl.textContent = formattedDate;
    }

    updateSyncStatusBadge();

    try {
      localStorage.setItem(STORAGE_LANG_KEY, currentLang);
    } catch (_) {
      // Ignore storage errors
    }
  }

  async function fetchJsonWithTimeout(url, timeoutMs = 8000, headers = {}) {
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), timeoutMs);
    try {
      const response = await fetch(url, {
        headers,
        signal: controller.signal,
        cache: "no-cache"
      });
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      return await response.json();
    } finally {
      clearTimeout(timer);
    }
  }

  async function syncLatestRelease() {
    // 1. Try live GitHub Releases API first for instant zero-maintenance sync
    try {
      const liveData = await fetchJsonWithTimeout(GITHUB_LATEST_API, 8000, {
        Accept: "application/vnd.github+json"
      });
      const info = extractReleaseInfo(liveData);
      if (info) {
        currentSyncMode = "live";
        applyReleaseToUI(info);
        updateSyncStatusBadge();
        return;
      }
    } catch (_) {
      // Fall through cleanly on network issue or API rate limit (HTTP 403)
    }

    // 2. Fallback to repo-hosted docs/latest-release.json (updated automatically by CI)
    try {
      const fallbackData = await fetchJsonWithTimeout(LOCAL_FALLBACK_JSON, 6000);
      const info = extractReleaseInfo(fallbackData);
      if (info) {
        currentSyncMode = "fallback";
        applyReleaseToUI(info);
        updateSyncStatusBadge();
        return;
      }
    } catch (_) {
      // Fall through cleanly to direct GitHub Releases URL
    }

    // 3. Final graceful fallback to official GitHub Releases latest page
    currentSyncMode = "direct";
    document.querySelectorAll(".js-apk-download").forEach((link) => {
      link.setAttribute("href", GITHUB_RELEASES_FALLBACK_URL);
      link.removeAttribute("download");
    });
    updateSyncStatusBadge();
  }

  function init() {
    const btnEn = document.getElementById("btn-lang-en");
    const btnBn = document.getElementById("btn-lang-bn");

    if (btnEn) btnEn.addEventListener("click", () => applyLanguage("en"));
    if (btnBn) btnBn.addEventListener("click", () => applyLanguage("bn"));

    applyLanguage(currentLang);
    syncLatestRelease();
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
