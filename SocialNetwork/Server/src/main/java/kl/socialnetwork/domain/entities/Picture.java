package kl.socialnetwork.domain.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pictures")
public class Picture extends BaseEntity {
    private String description;
//    private Album album;
    private User user;
    private String imageUrl;
    private LocalDateTime time;

    public Picture() {
    }

    @Column(name = "description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(optional = false, targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "image_url", nullable = false)
    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Column(name = "time", nullable = false)
    public LocalDateTime getTime() {
        return this.time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }



    //    @ManyToOne(optional = false, targetEntity = Album.class)
//    @JoinColumn(name = "album_id", referencedColumnName = "id")
//    public Album getAlbum() {
//        return this.album;
//    }
//
//    public void setAlbum(Album album) {
//        this.album = album;
//    }

}
