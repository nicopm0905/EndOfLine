-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT INTO authorities(id,authority) VALUES (1,'ADMIN');
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (1, 'User', 'admin1','$2a$10$nMmTWAhPTqXqLDJTag3prumFrAJpsYtroxf0ojesFYq0k4PmcbWUS','Admin','User','admin1@email.com',1,1);

-- Ten player users, named player1 with password 0wn3r
INSERT INTO authorities(id,authority) VALUES (2,'PLAYER');
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (4, 'Player', 'player1','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','One','player1@email.com',1,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (5, 'Player', 'player2','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','Two','player2@email.com',2,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (6, 'Player', 'player3','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','Three','player3@email.com',3,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (7, 'Player', 'player4','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','Four','player4@email.com',4,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (8, 'Player', 'player5','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','Five','player5@email.com',5,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (9, 'Player', 'player6','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','Six','player6@email.com',6,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (10, 'Player', 'player7','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','Seven','player7@email.com',7,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (11, 'Player', 'player8','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','Eight','player8@email.com',1,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (12, 'Player', 'player9','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','Nine','player9@email.com',2,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (13, 'Player', 'player10','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Player','Ten','player10@email.com',3,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (14, 'Player', 'YGC9995','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Nicolás','Perez','ygc9995@email.com',4,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (15, 'Player', 'JBV8381','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Carmen','Camacho','jbv8381@email.com',5,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (16, 'Player', 'alepicmar','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Alejandro','Pichardo','alepicmar@email.com',6,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (17, 'Player', 'WBK2747','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Álvaro','de Pablos','wbk2747@email.com',7,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (18, 'Player', 'DKK2084','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Juan','Pozo','dkk2084@email.com',1,2);
INSERT INTO appusers(id, DTYPE, username, password, first_name, last_name, email, avatar_id, authority) VALUES (19, 'Player', 'QHR9543','$2a$10$DaS6KIEfF5CRTFrxIoGc7emY3BpZZ0.fVjwA3NiJ.BjpGNmocaS3e','Francisco','Casasola','qhr9543@email.com',2,2);

INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (1,'Winner','Win 10 games',10.0,'https://cdn-icons-png.flaticon.com/512/1332/1332056.png','VICTORIES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (2,'Long Arrow','Make your line cover 15 squares',15.0,'https://www.pngfind.com/pngs/m/681-6816527_icono-de-flecha-png-transparent-png.png','MAX_LINE_LENGTH');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (3,'Seeker','Play 25 games',25.0,'https://cdn-icons-png.flaticon.com/512/486/486379.png','GAMES_PLAYED');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (4,'Friends','Make 5 friends',5.0,'https://cdn-icons-png.flaticon.com/512/2618/2618623.png','FRIENDS_COUNT');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (5,'Champion','Win 50 games',50.0,'https://png.pngtree.com/png-clipart/20220615/original/pngtree-king-crown-png-in-flat-style-png-image_8046791.png','VICTORIES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (6,'Companion','Play 10 games',10.0,'https://cdn-icons-png.flaticon.com/512/1006/1006656.png','GAMES_PLAYED');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (7,'Streak','Win 25 games',25.0,'https://cdn-icons-png.flaticon.com/512/1332/1332056.png','VICTORIES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (8,'Veteran','Play 50 games',50.0,'https://cdn-icons-png.flaticon.com/512/1006/1006656.png','GAMES_PLAYED');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (9,'Line Master','Make your line cover 20 squares',20.0,'https://www.pngfind.com/pngs/m/681-6816527_icono-de-flecha-png-transparent-png.png','MAX_LINE_LENGTH');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (10,'Puzzle Rookie','Complete 5 puzzles',5.0,'https://cdn-icons-png.flaticon.com/512/486/486379.png','COMPLETED_PUZZLES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (11,'Puzzle Pro','Complete 15 puzzles',15.0,'https://cdn-icons-png.flaticon.com/512/486/486379.png','COMPLETED_PUZZLES');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (12,'Puzzle Ace','Score 1000 in a puzzle',1000.0,'https://png.pngtree.com/png-clipart/20220615/original/pngtree-king-crown-png-in-flat-style-png-image_8046791.png','HIGHEST_SCORE_PUZZLE');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (13,'Puzzle Legend','Score 2500 in a puzzle',2500.0,'https://png.pngtree.com/png-clipart/20220615/original/pngtree-king-crown-png-in-flat-style-png-image_8046791.png','HIGHEST_SCORE_PUZZLE');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (14,'Timekeeper','Play for 2 hours total',7200.0,'https://cdn-icons-png.flaticon.com/512/1006/1006656.png','TOTAL_PLAY_TIME');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (15,'Marathoner','Play for 5 hours total',18000.0,'https://cdn-icons-png.flaticon.com/512/1006/1006656.png','TOTAL_PLAY_TIME');
INSERT INTO achievement(id,name,description,threshold,badge_image,metric) VALUES (17,'Socializer','Make 10 friends',10.0,'https://cdn-icons-png.flaticon.com/512/2618/2618623.png','FRIENDS_COUNT');


INSERT INTO game_sessions (id, name, host, game_mode, state, winner, start_time, end_time, is_private, round, board_size, max_players) -- <-- Columna añadida
VALUES 
(3, 'miPuzzle', 6 , 'SOLITARY_PUZZLE', 'FINISHED', 6, '2025-10-04T18:00:00', '2025-10-04T18:21:00', false, 1, 5, 1);

-- IMPORTANTE: NO BORRAR YA QUE SON PARA TEST
INSERT INTO player_game_sessions (id, player_id, game_session_id, player_color, energy) 
VALUES (1, 6, 3, 'RED', 3);
   


-- ============================================
-- PLAYER STATISTICS (VERSIÓN CORREGIDA)
-- ============================================

-- Admin (sin actividad)
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    1, 1,
    0, 0, 0,
    0, 999999, 0,
    0, 0, NULL, NULL, 0,
    0, 0, 0.0,
    0, 0, 0,
    0, 0, 0,
    0, 0
);

-- player1 (id=4) - Jugador casual
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    2, 4,
    5, 2, 3,
    3600, 600, 900,
    45, 12, 'SPEED_UP', 'RED', 18,
    8, 15, 6.5,
    1200, 3, 3100,
    850, 220, 140,
    23, 2
);

-- player2 (id=5) - Jugador intermedio
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    3, 5,
    12, 7, 5,
    7200, 480, 850,
    89, 25, 'BRAKING', 'BLUE', 35,
    14, 38, 8.2,
    2450, 7, 14200,
    2150, 380, 120,
    67, 5
);

-- player3 (id=6) - Jugador principiante
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    4, 6,
    3, 1, 2,
    1800, 550, 720,
    23, 8, 'EXTRA_GAS', 'GREEN', 10,
    6, 9, 5.8,
    780, 1, 780,
    420, 180, 100,
    12, 1
);

-- player4 (id=7) - Jugador avanzado
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    5, 7,
    18, 9, 9,
    9000, 420, 780,
    120, 38, 'SPEED_UP', 'RED', 52,
    13, 54, 9.1,
    3100, 9, 25800,
    3200, 420, 130,
    89, 7
);

-- player5 (id=8) - Jugador intermedio
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    6, 8,
    8, 3, 5,
    4800, 500, 820,
    56, 18, 'BRAKING', 'YELLOW', 22,
    9, 24, 7.3,
    1625, 4, 5800,
    1450, 310, 110,
    34, 3
);

-- player6 (id=9) - Jugador experimentado
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    7, 9,
    15, 6, 9,
    8400, 450, 800,
    95, 30, 'SPEED_UP', 'BLUE', 40,
    11, 42, 8.5,
    2100, 6, 11400,
    2650, 360, 125,
    72, 4
);

-- player7 (id=10) - Jugador casual
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    8, 10,
    6, 2, 4,
    3600, 520, 750,
    48, 15, 'EXTRA_GAS', 'GREEN', 20,
    7, 18, 6.8,
    1350, 2, 2500,
    980, 240, 130,
    28, 2
);

-- player8 (id=11) - Jugador experto ⭐
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    9, 11,
    20, 11, 9,
    12000, 400, 900,
    140, 45, 'SPEED_UP', 'RED', 65,
    15, 60, 10.2,
    3550, 11, 37200,
    4200, 480, 140,
    105, 8
);

-- player9 (id=12) - Jugador intermedio
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    10, 12,
    9, 4, 5,
    5400, 480, 780,
    67, 20, 'BRAKING', 'YELLOW', 28,
    10, 27, 7.8,
    1750, 4, 6200,
    1620, 320, 115,
    41, 3
);

-- player10 (id=13) - Jugador intermedio
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    11, 13,
    11, 5, 6,
    6600, 460, 820,
    78, 24, 'EXTRA_GAS', 'GREEN', 32,
    12, 33, 8.4,
    2260, 5, 10100,
    1980, 340, 120,
    52, 4
);

-- YGC9995 (id=14) - JUGADOR TOP ⭐⭐⭐
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    12, 14,
    30, 16, 14,
    18000, 380, 950,
    210, 68, 'SPEED_UP', 'RED', 98,
    17, 90, 11.5,
    3980, 14, 51200,
    6500, 550, 150,
    142, 10
);

-- JBV8381 (id=15) - Jugador intermedio
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    13, 15,
    7, 3, 4,
    4200, 490, 790,
    52, 16, 'BRAKING', 'BLUE', 21,
    9, 21, 7.5,
    1480, 3, 4100,
    1260, 280, 105,
    31, 2
);

-- alepicmar (id=16) - Jugador avanzado ⭐⭐
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    14, 16,
    22, 12, 10,
    13200, 410, 880,
    165, 52, 'SPEED_UP', 'RED', 72,
    16, 66, 10.8,
    3725, 12, 41500,
    4850, 510, 135,
    98, 9
);

-- WBK2747 (id=17) - Jugador experimentado ⭐
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    15, 17,
    14, 7, 7,
    8400, 440, 850,
    98, 32, 'EXTRA_GAS', 'YELLOW', 42,
    13, 42, 9.2,
    2440, 8, 18000,
    3100, 420, 125,
    65, 6
);

-- DKK2084 (id=18) - Jugador experto ⭐⭐⭐
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    16, 18,
    25, 13, 12,
    15000, 390, 920,
    180, 58, 'BRAKING', 'BLUE', 82,
    15, 75, 10.5,
    3890, 13, 47800,
    5500, 520, 145,
    118, 9
);

-- QHR9543 (id=19) - Jugador intermedio
INSERT INTO player_statistics (
    id, player_id,
    games_played, victories, defeats,
    total_play_time, shortest_game, longest_game,
    total_cards_used, power_cards_used, power_most_used, favorite_color, favorite_color_uses,
    max_line_length, total_lines_completed, average_line_length,
    highest_score_puzzle, completed_puzzles, total_puzzle_score,
    total_score, highest_score, lowest_score,
    messages_sent, friends_count
) 
VALUES (
    17, 19,
    10, 5, 5,
    6000, 470, 810,
    72, 22, 'SPEED_UP', 'GREEN', 30,
    11, 30, 8.0,
    1880, 5, 8500,
    1800, 350, 120,
    48, 4
);

-- ============================================
-- PLAYER ACHIEVEMENTS (Sin cambios, parece correcto)
-- ============================================
-- player1 (id=4) - 5 partidas jugadas, 2 victorias
INSERT INTO player_achievements (id, progress, completed, unlocked_at, player_id, achievement_id) 
VALUES 
(1, 2, false, NULL, 4, 1),  -- Ganador (necesita 10 victorias)
(2, 5, false, NULL, 4, 3);  -- Buscador (necesita 25 partidas)

-- player2 (id=5) - 12 partidas jugadas, 7 victorias, línea máxima 14
INSERT INTO player_achievements (id, progress, completed, unlocked_at, player_id, achievement_id) 
VALUES 
(3, 7, false, NULL, 5, 1),                           -- Ganador (7/10)
(4, 12, false, NULL, 5, 3),                          -- Buscador (12/25)
(5, 14, false, NULL, 5, 2);                          -- Flecha Larga (14/15)

-- player4 (id=7) - 18 partidas jugadas, 9 victorias, línea máxima 13
INSERT INTO player_achievements (id, progress, completed, unlocked_at, player_id, achievement_id) 
VALUES 
(6, 9, false, NULL, 7, 1),                           -- Ganador (9/10)
(7, 18, false, NULL, 7, 3),                          -- Buscador (18/25)
(8, 13, false, NULL, 7, 2);                          -- Flecha Larga (13/15)

-- player8 (id=11) - 20 partidas jugadas, 11 victorias, línea máxima 15 ✅
INSERT INTO player_achievements (id, progress, completed, unlocked_at, player_id, achievement_id) 
VALUES 
(9, 11, true, '2025-01-09 13:25:00', 11, 1),        -- Ganador ✅
(10, 20, false, NULL, 11, 3),                        -- Buscador (20/25)
(11, 15, true, '2025-01-17 10:50:00', 11, 2);       -- Flecha Larga ✅

-- YGC9995 (id=14) - El jugador más avanzado: 30 partidas, 16 victorias, línea 17 ✅
INSERT INTO player_achievements (id, progress, completed, unlocked_at, player_id, achievement_id) 
VALUES 
(12, 16, true, '2025-01-05 12:00:00', 14, 1),       -- Ganador ✅
(13, 30, true, '2025-01-12 15:30:00', 14, 3),       -- Buscador ✅
(14, 17, true, '2025-01-20 14:20:00', 14, 2),       -- Flecha Larga ✅
(15, 16, false, NULL, 14, 5);                        -- Campeón (16/50)

-- alepicmar (id=16) - Buen jugador: 22 partidas, 12 victorias, línea 16 ✅
INSERT INTO player_achievements (id, progress, completed, unlocked_at, player_id, achievement_id) 
VALUES 
(16, 12, true, '2025-01-11 16:40:00', 16, 1),       -- Ganador ✅
(17, 22, false, NULL, 16, 3),                        -- Buscador (22/25)
(18, 16, true, '2025-01-19 09:15:00', 16, 2);       -- Flecha Larga ✅

-- DKK2084 (id=18) - Jugador avanzado: 25 partidas ✅, 13 victorias, línea 15 ✅
INSERT INTO player_achievements (id, progress, completed, unlocked_at, player_id, achievement_id) 
VALUES 
(19, 13, true, '2025-01-13 11:30:00', 18, 1),       -- Ganador ✅
(20, 25, true, '2025-01-18 10:00:00', 18, 3),       -- Buscador ✅
(21, 15, true, '2025-01-16 15:20:00', 18, 2),       -- Flecha Larga ✅
(22, 13, false, NULL, 18, 5);                        -- Campeón (13/50)

-- WBK2747 (id=17) - Progreso medio: 14 partidas, 7 victorias, línea 13
INSERT INTO player_achievements (id, progress, completed, unlocked_at, player_id, achievement_id) 
VALUES 
(23, 7, false, NULL, 17, 1),                         -- Ganador (7/10)
(24, 14, false, NULL, 17, 3),                        -- Buscador (14/25)
(25, 13, false, NULL, 17, 2);                        -- Flecha Larga (13/15)


-- Encuentra qué player_ids existen en player_statistics pero no en appusers
SELECT DISTINCT ps.player_id 
FROM player_statistics ps 
LEFT JOIN appusers p ON ps.player_id = p.id 
WHERE p.id IS NULL;

-- Elimina esas estadísticas huérfanas
DELETE FROM player_statistics 
WHERE player_id NOT IN (SELECT id FROM appusers WHERE dtype='Player');

