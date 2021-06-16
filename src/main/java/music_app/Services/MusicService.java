package music_app.Services;

import music_app.Model.Music;
import music_app.Model.User;
import music_app.Repositories.MusicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("MusicService")
public class MusicService {
    @Autowired
    MusicRepository musicRepository;

    @Transactional
    public void save(Music music){
        musicRepository.save(music);
    }
    @Transactional
    public void delete(int id){
        musicRepository.deleteById(id);
    }
    @Transactional
    public List<Music> findAll(){
        return musicRepository.findByIsdisplay(true);
    }
    @Transactional
    public Music findById(int id){
        return musicRepository.findById(id);
    }
    @Transactional
    public List<Music> findByUser(User user){
        return musicRepository.findByUser(user);
    }
    @Transactional
    public List<Music> findByTitleAndCategory(String title,String category){
        return musicRepository.findByTitleAndCategoryAndIsdisplay(title,category,true);
    }
    @Transactional
    public List<Music> findByCategory(String category){
        return musicRepository.findByCategoryAndIsdisplay(category,true);
    }
    @Transactional
    public List<Music> findByTitleAndCategoryAndUser(String title,String category,User user){
        return musicRepository.findByTitleAndCategoryAndUser(title, category, user);
    }
    @Transactional
    public List<Music> findByCategoryAndUser(String category,User user){
        return musicRepository.findByCategoryAndUser(category, user);
    }
    @Transactional
    public List<Music> findByIsdisplay(boolean isdisplay){
        return musicRepository.findByIsdisplay(isdisplay);
    }
    @Transactional
    public List<Music> findByIsdisplayAndTitle(boolean isdisplay,String title){
        return musicRepository.findByIsdisplayAndTitle(isdisplay,title);
    }
}
