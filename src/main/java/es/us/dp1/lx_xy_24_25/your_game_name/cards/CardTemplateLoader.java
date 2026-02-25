package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CardTemplateLoader implements CommandLineRunner {

    private final CardTemplateRepository cardTemplateRepository;

    @Override
    public void run(String... args) throws Exception {
        long lineCount = cardTemplateRepository.findAll().stream()
            .filter(t -> t.getType() == CardType.LINE)
            .count();
        
        if (lineCount < 8) {
            cardTemplateRepository.deleteAll();
            
            if (cardTemplateRepository.count() == 0) {
            List<CardTemplate> templates = List.of(
                create(CardType.LINE, 2, Orientation.S, Set.of(Orientation.E), 1),                          
                create(CardType.LINE, 1, Orientation.S, Set.of(Orientation.N), 2),                           
                create(CardType.LINE, 3, Orientation.S, Set.of(Orientation.N, Orientation.E), 3),                           
                create(CardType.LINE, 2, Orientation.S, Set.of(Orientation.W), 4),             
                create(CardType.LINE, 4, Orientation.S, Set.of(Orientation.W, Orientation.E), 5),             
                create(CardType.LINE, 3, Orientation.S, Set.of(Orientation.N, Orientation.W), 6),             
                create(CardType.LINE, 0, Orientation.S, Set.of(Orientation.N, Orientation.E, Orientation.W), 7),           
                create(CardType.LINE, 5, Orientation.S, Set.of(Orientation.N, Orientation.W, Orientation.E), 8),
                create(CardType.START, 0, Orientation.S, Set.of(Orientation.N),999),
                create(CardType.BACK, 0, Orientation.S, Set.of(),10)
            );

            templates.forEach(cardTemplateRepository::save);
            }
        }
    }

    private CardTemplate create(CardType type, int initiative, Orientation entrance, Set<Orientation> exits, int imageId) {
        CardTemplate ct = new CardTemplate();
        ct.setType(type);
        ct.setInitiative(initiative);
        ct.setDefaultEntrance(entrance);
        ct.setDefaultExits(exits);
        ct.setImageId(imageId);
        return ct;
    }
}
