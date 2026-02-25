package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardTemplateRepository extends JpaRepository<CardTemplate, Integer> {
    @Query("SELECT ct FROM CardTemplate ct WHERE ct.type = ?1")
    java.util.Optional<CardTemplate> findByType(CardType type);

     @Query("SELECT ct FROM CardTemplate ct WHERE ct.type = ?1")
    List<CardTemplate> findAllByType(CardType type);
}
