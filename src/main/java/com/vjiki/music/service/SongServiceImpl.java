package com.vjiki.music.service;

import com.vjiki.music.dto.SongResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SongServiceImpl implements SongService {

    @Override
    public List<SongResponse> getSongs(String userId) {
        List<SongResponse> songs = new ArrayList<>();
        
        // Demo songs
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Diljit Dosanjh",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "https://picsum.photos/200?random=1",
            "GOAT",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Arijit Singh",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "https://picsum.photos/200?random=2",
            "Hai Kude",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Armaan Malik",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            "https://picsum.photos/200?random=3",
            "Making Memories",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Jasleen Royal",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            "https://picsum.photos/200?random=4",
            "Heeriye",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Jordan Sandhu",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            "https://picsum.photos/200?random=5",
            "Hateraan",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "B Praak",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            "https://picsum.photos/200?random=6",
            "Duppatta",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Arijit Singh",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            "https://picsum.photos/200?random=7",
            "Just Friend",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Darshan Raval",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            "https://picsum.photos/200?random=8",
            "Tera Zikr",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Karan Randhawa",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
            "https://picsum.photos/200?random=9",
            "Pulkaari",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Parmish Verma",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
            "https://picsum.photos/200?random=10",
            "Teri Aakh",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Neha Kakkar",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3",
            "https://picsum.photos/200?random=11",
            "Distance Love",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Shubh",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
            "https://picsum.photos/200?random=12",
            "One Love",
            false,
            false
        ));
        
        // Scotch songs
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1Lyi-7lOLELU6sAjd3D9saShX6Hza56dK",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Шаг в темноту",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1kA19c1o1eK0ZFlCslmU6hmm-Tuot9hsA",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Реки слез",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1fyqbJobeUTZeaIP5b9ece5pUbVpAJG7O",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Цветы зла",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1eqERA3CsWVnzIHSeZKw2Y1tq14AOYizp",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Домино",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1qzlR0z8JCSQb8eTwYk7pODm8003DLj8p",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Звезды",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=19x6DQTF4lJ-T-Lq5s5poyVFLhN3dCFH-",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Падение",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1eQQRSq7hlk7YoMALUVfy6S9ivRhm5fA3",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Твой Мир",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1veW1fEVD-5wqd-_G8EC7ACVVC1D8jrV3",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Лето без тебя",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1hvzblDwo3pbHzTt7SKGSo4iBpBxBi9sq",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Горизонты",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1tfS0PN9_2jaP7xHs_eDu5hSF8zyOhu1z",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Холод стен",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "Scotch",
            "https://drive.google.com/uc?export=download&id=1zz2uJavwLDsgzGNWq-gE8nnrZ8_F3J2i",
            "https://drive.google.com/uc?export=download&id=1pYVYMKPWFoBqof8e_bLCpfME8rwekpLd",
            "Не может длиться вечно",
            false,
            false
        ));
        
        // 7раса songs
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "7раса",
            "https://drive.google.com/uc?export=download&id=1etxvzJXhdinj121-3fjNbq6bnh9PWIuL",
            "https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-",
            "1й круг",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "7раса",
            "https://drive.google.com/uc?export=download&id=1vLi0-msdE7ywANn6TWlu1SplKEEI9EX4",
            "https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-",
            "В поисках рая",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "7раса",
            "https://drive.google.com/uc?export=download&id=1Mg9bfPmczARpO_BNReFb0lS--ygYQWKz",
            "https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-",
            "Вечное лето",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "7раса",
            "https://drive.google.com/uc?export=download&id=1nF-RzvuywP0vvBxQQw21WEORxbcf7l2S",
            "https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-",
            "Качели",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "7раса",
            "https://drive.google.com/uc?export=download&id=1rcCbVoe11he3IecVK5soYhdaoUylkAVc",
            "https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-",
            "Три цвета",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "7раса",
            "https://drive.google.com/uc?export=download&id=1UoZSGUrEmbKXmh1vz4viq2tp7wwhOVVR",
            "https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-",
            "Ты или я",
            false,
            false
        ));
        
        songs.add(new SongResponse(
            UUID.randomUUID().toString(),
            "7раса",
            "https://drive.google.com/uc?export=download&id=1b8mdnW9VsTk4AsIe01y8a5x3LFCqLkY7",
            "https://drive.google.com/uc?export=download&id=1okM0wJHIasHmJFOGAkfMy6MtLRqVnr8-",
            "Черная весна",
            false,
            false
        ));
        
        // TODO: In the future, you can use userId to fetch user-specific favourite/disliked status
        // For now, returning all songs with default false values
        
        return songs;
    }
}

