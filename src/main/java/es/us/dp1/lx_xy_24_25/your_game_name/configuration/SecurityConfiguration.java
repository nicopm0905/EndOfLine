package es.us.dp1.lx_xy_24_25.your_game_name.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthEntryPointJwt;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.AuthTokenFilter;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Autowired
	DataSource dataSource;

	private static final String ADMIN = "ADMIN";
	private static final String PLAYER = "PLAYER";


	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

		http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                .requestMatchers("/", "/oups").permitAll()

                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**"
                ).permitAll()

                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/developers").permitAll()
                .requestMatchers("/api/v1/plan").permitAll()
                .requestMatchers("/api/v1/achievements").permitAll()
                .requestMatchers("/ws/**").permitAll()
                

                .requestMatchers(HttpMethod.GET,"/api/v1/gameList").authenticated()
                .requestMatchers(HttpMethod.GET,"/api/v1/gameList/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/gameList").authenticated()
                .requestMatchers(HttpMethod.DELETE,"/api/v1/gameList/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/gameList").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/gameList/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/gameList/:id/play**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/gameList/**").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/v1/gameList/:id/drawdeck").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/v1/gameList/:id/draw-discard").authenticated()
                .requestMatchers(HttpMethod.POST,"/api/v1/gameList/:id/discard").authenticated()          
                .requestMatchers(HttpMethod.GET,"/api/v1/achievements/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/statistics/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/statistics/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/statistics/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/player-achievements/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/player-achievements/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/player-achievements/**").authenticated()
                .requestMatchers("/api/v1/invitations/**").hasAuthority("PLAYER")
                .requestMatchers("/api/v1/gamesessions/*/place-card").authenticated()
                .requestMatchers("/api/v1/gamesessions/*/placed-cards").authenticated()
                .requestMatchers("/api/v1/gamesessions/*/placed-cards/position").authenticated()
 
                .requestMatchers("/api/v1/players/**").authenticated()
                .requestMatchers(HttpMethod.GET,"/api/v1/friendships/all").authenticated()
                .requestMatchers("/api/v1/friendships/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/chat/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/chat/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/cards/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/v1/cards/**").permitAll()

                .requestMatchers(HttpMethod.GET,"/api/v1/achievements/{achievementId}/progress").hasAuthority(ADMIN)
                .requestMatchers(HttpMethod.POST,"/api/v1/achievements/**").hasAuthority(ADMIN)
                .requestMatchers(HttpMethod.PUT,"/api/v1/achievements/**").hasAuthority(ADMIN)
                .requestMatchers(HttpMethod.DELETE,"/api/v1/achievements/**").hasAuthority(ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/api/v1/statistics/**").hasAuthority(ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/api/v1/player-achievements/**").hasAuthority(ADMIN)
                


                .requestMatchers(HttpMethod.GET, "/api/v1/users/username/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/users/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").authenticated()
                
                .requestMatchers("/api/v1/users/**").hasAuthority(ADMIN)


                .anyRequest().denyAll()
            )


			.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
