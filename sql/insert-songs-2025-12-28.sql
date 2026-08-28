-- Insert songs into music.songs table
-- Google Drive URLs converted to direct download format (public links required)

-- Feduk, Баста, Моя Мишель - Хлопья летят наверх
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Хлопья летят наверх',
    '{"default":["Feduk", "Баста", "Моя Мишель"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1CgIEsjNuNyL2K5OuzjFxNpKRD0GA2dIO"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=132fQ0mAWJ2fOT7MyAGuJ4zGv8e68mWIJ"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Пачка Сигарет - 101 Причина
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    '101 Причина',
    '{"default":["Пачка Сигарет"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1FhNuf_81LUMZglMt_MoQwl2JWIAAPnEr"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1Itgdr05EF5J917DkhHsVAoAHQohz4Q7_"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Пачка Сигарет - Голос
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Голос',
    '{"default":["Пачка Сигарет"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=14j3-2IfnybMJnRnwNj8A542mCwPPxLsj"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1Itgdr05EF5J917DkhHsVAoAHQohz4Q7_"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Пачка Сигарет - Малина
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Малина',
    '{"default":["Пачка Сигарет"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1LLzal0xDeRFn29WK7lHcAoLeL5MSiqlc"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1Itgdr05EF5J917DkhHsVAoAHQohz4Q7_"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Пачка Сигарет - Сердце
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Сердце',
    '{"default":["Пачка Сигарет"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1Oh-RB8QjtKB0KF6444K4K91RYA2ZDCJl"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1Itgdr05EF5J917DkhHsVAoAHQohz4Q7_"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Моя Мишель - Ветер меняет направление
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Ветер меняет направление',
    '{"default":["Моя Мишель"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1lxb-9q70eAJ6_z8x2nOtPDczJXlTz8FL"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1g6KX0hJvMi5leT6VNtGyW4-H0gL6Mexs"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Моя Мишель - Зима в сердце
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Зима в сердце',
    '{"default":["Моя Мишель"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1wfZA0PQNUL_yyqKAzn-mfdiYHoBUxvgw"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1g6KX0hJvMi5leT6VNtGyW4-H0gL6Mexs"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Моя Мишель, ЛСП - Курточка
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Курточка',
    '{"default":["Моя Мишель", "ЛСП"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=11RiM1VZrW3CAiVJKEtPQYavoftPyGMcc"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1g6KX0hJvMi5leT6VNtGyW4-H0gL6Mexs"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- MORGENSHTERN - Последняя любовь
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Последняя любовь',
    '{"default":["MORGENSHTERN"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1FKXs3qGVIdhUt7BKxS5tYmd5pbkUFT3U"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1d1XqqJPV2NvXiDQeFbvjwE3AbG2WLJcJ"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Виктор Цой - Пачка Сигарет
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Пачка Сигарет',
    '{"default":["Виктор Цой"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=13ErwKIGwOMC50rYFwUEl6UiNiz8Sr9cA"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1VQSg5OoH6T54RlZvHFVZxZJ0tXfYF8yN"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Юлия Савичева, Raush - Бабочки
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Бабочки',
    '{"default":["Юлия Савичева", "Raush"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1NRhjqbO2nDEucvIxr8cHXhbbAo8IGyxZ"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1LsEMP2DojRJazWEX8Saj1fsnws7qppxO"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Betsy, Ded M - Под Новый Год
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Под Новый Год',
    '{"default":["Betsy", "Ded M"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1Ow-mvWxm_lXwPiNFowmm2ZIYgbHTYLF8"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1WRcrS-Dej_-f5QFl8q4T796u27-aZVEn"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Братство Атома, BassnPanda, Квашеная, Atomic heart - Звенит январская вьюга
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Звенит январская вьюга',
    '{"default":["Братство Атома", "BassnPanda", "Квашеная", "Atomic heart"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1t5hJtkFEpTpn2qjGaCvGcjKMulToJxXP"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1cMMTOwqtDGPisuDBlOd2wvgKx3W0RKUE"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Братство Атома, BassnPanda, Квашеная, Atomic heart - На заре
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'На заре',
    '{"default":["Братство Атома", "BassnPanda", "Квашеная", "Atomic heart"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1Wzmjcz3v9gGgsVtwgL_HEWl6V_P5Xynk"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=19bLDKNTjAMZEx2OXgQAGkLiOFEwfGF_z"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- LeanJe - Любила летать
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Любила летать',
    '{"default":["LeanJe"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1HgY5tl0qmCGip8NQ8WaoQjaCRIRwaJ5B"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1d3lez4iEjF5xsW1feJ-XNTdsc52PUblO"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- XOLIDAYBOY - Пожары
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Пожары',
    '{"default":["XOLIDAYBOY"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1ZOM0WO3k_fHdFoSHQ9pTmF59pVO-VDAp"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1T36csvHLpkMo0X5df9FW7gS_2rAC12fA"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- ODURACHEN - Крутится
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Крутится',
    '{"default":["ODURACHEN"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=135kxS7WM-OhMlNDJrRoTRjyE6H31b-w6"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1GTaL1z-w9uMWlOv7ErnkGGlAtQaEJ0sy"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Честный - Боже просвяти путь
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Боже просвяти путь',
    '{"default":["Честный"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1v9nOyEy20n5QtL-Bu8zLSoKw5_aNz7eW"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1KLcH758vNVwgHcG4rZoF2RQJhcTAtWvu"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Честный - Да гори оно огнем
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Да гори оно огнем',
    '{"default":["Честный"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1NqWhDbe5seok42_pO1IfWcxKcppY8XL2"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1KLcH758vNVwgHcG4rZoF2RQJhcTAtWvu"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Честный - Добрый насквозь
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Добрый насквозь',
    '{"default":["Честный"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1RToZPZfOEYTUoJFHgo16bu_fLsjD0j3-"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1KLcH758vNVwgHcG4rZoF2RQJhcTAtWvu"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Честный - Каплями дождя
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Каплями дождя',
    '{"default":["Честный"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1Ejh9PBh9xsmuTWzbgd6ZRSzLLMtFTNWu"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1KLcH758vNVwgHcG4rZoF2RQJhcTAtWvu"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Честный - Маями
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Маями',
    '{"default":["Честный"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1EbiVl0KFFpBNX_zoZm_a6V2aHuAw3Vul"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1KLcH758vNVwgHcG4rZoF2RQJhcTAtWvu"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Честный - Прости мама
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Прости мама',
    '{"default":["Честный"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1LVhApEFNeDRGehMYbWb8gruo37OiVlft"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1KLcH758vNVwgHcG4rZoF2RQJhcTAtWvu"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Честный - Разлетайтесь мыши
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'Разлетайтесь мыши',
    '{"default":["Честный"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1HG4ivL8BSDpgC-LgLSMtnhEhlyhL3x-E"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1KLcH758vNVwgHcG4rZoF2RQJhcTAtWvu"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);

-- Честный - За спиной
INSERT INTO music.songs (
    title,
    artists,
    audio_urls,
    cover_urls,
    created_by,
    modified_by,
    version,
    likes_count,
    dislikes_count,
    access_level,
    active
) VALUES (
    'За спиной',
    '{"default":["Честный"]}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1Gx7NGlc8J4X9pWnOEPZ8YqybxBoVEq8Z"}'::jsonb,
    '{"default":"https://drive.google.com/uc?export=download&id=1KLcH758vNVwgHcG4rZoF2RQJhcTAtWvu"}'::jsonb,
    'system',
    'system',
    0,
    0,
    0,
    'USER',
    true
);


