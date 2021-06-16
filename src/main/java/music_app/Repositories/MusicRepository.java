package music_app.Repositories;

import music_app.Model.Music;
import music_app.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("MusicRepository")
public interface MusicRepository extends JpaRepository<Music,Integer> {
    Music findById(int id);
    List<Music> findByUser(User user);
    List<Music> findByTitleAndCategoryAndIsdisplay(String title,String category,boolean isdisplay);
    List<Music> findByCategoryAndIsdisplay(String category,boolean isdisplay);
    List<Music> findByTitleAndCategoryAndUser(String title,String category,User user);
    List<Music> findByCategoryAndUser(String category,User user);
    List<Music> findByIsdisplay(boolean isdisplay);
    List<Music> findByIsdisplayAndTitle(boolean isdisplay,String title);
}
