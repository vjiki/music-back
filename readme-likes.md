Define your JPA entities
Assuming your schema:
songs table
likes table
dislikes table
Here’s a simplified example:
@Entity
@Table(name = "songs", schema = "music")
public class Song {
@Id
private UUID id;

    private String title;

    @Column(name = "likes_count")
    private Long likesCount;

    @Column(name = "dislikes_count")
    private Long dislikesCount;

    // getters & setters
}

@Entity
@Table(name = "likes", schema = "music")
public class Like {
@Id
private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    // getters & setters
}

@Entity
@Table(name = "dislikes", schema = "music")
public class Dislike {
@Id
private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "song_id")
    private Song song;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    // getters & setters
}
2️⃣ Spring Data Repositories
public interface SongRepository extends JpaRepository<Song, UUID> {}

public interface LikeRepository extends JpaRepository<Like, UUID> {
boolean existsByUserIdAndSongIdAndRevokedAtIsNull(UUID userId, UUID songId);
}

public interface DislikeRepository extends JpaRepository<Dislike, UUID> {
boolean existsByUserIdAndSongIdAndRevokedAtIsNull(UUID userId, UUID songId);
}
3️⃣ Service methods
a) Like a song
@Service
@RequiredArgsConstructor
public class SongService {

    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;

    public void likeSong(UUID userId, UUID songId) {
        // Check if user already liked
        if (likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)) {
            return; // already liked
        }

        // Revoke dislike if exists
        dislikeRepository.findAll().stream()
            .filter(d -> d.getUser().getId().equals(userId) && d.getSong().getId().equals(songId) && d.getRevokedAt() == null)
            .forEach(d -> d.setRevokedAt(OffsetDateTime.now()));

        // Save new like
        Like like = new Like();
        like.setUser(new User(userId));
        like.setSong(new Song(songId));
        likeRepository.save(like);
    }

    public void dislikeSong(UUID userId, UUID songId) {
        if (dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId)) {
            return;
        }

        // Revoke like if exists
        likeRepository.findAll().stream()
            .filter(l -> l.getUser().getId().equals(userId) && l.getSong().getId().equals(songId) && l.getRevokedAt() == null)
            .forEach(l -> l.setRevokedAt(OffsetDateTime.now()));

        Dislike dislike = new Dislike();
        dislike.setUser(new User(userId));
        dislike.setSong(new Song(songId));
        dislikeRepository.save(dislike);
    }
}
b) Fetch likes/dislikes information for a song
You have two options:
Use likes_count and dislikes_count columns (fast, updated via triggers):
Song song = songRepository.findById(songId).orElseThrow();
Long likes = song.getLikesCount();
Long dislikes = song.getDislikesCount();
Calculate dynamically (if you want real-time accuracy):
Long likes = likeRepository.countBySongIdAndRevokedAtIsNull(songId);
Long dislikes = dislikeRepository.countBySongIdAndRevokedAtIsNull(songId);
✅ Optional: Check if a user liked/disliked a song
boolean liked = likeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);
boolean disliked = dislikeRepository.existsByUserIdAndSongIdAndRevokedAtIsNull(userId, songId);
This is useful for displaying UI buttons (like “Liked” or “Disliked”).
If you want, I can provide a full ready-to-use Spring Boot service with like, dislike, unlike, and song info endpoints that uses your existing database schema.