package com.services;

import com.entities.Film;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repositories.FilmRepository;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * Appelle l'API films des voisins pour récupérer un film par titre.
 * Si trouvé, le film est sauvegardé en base locale pour les prochaines fois.
 */
@Service
public class FilmServiceClient {

    private static final String FILMS_API = "http://info-tpsi:11041/movies";

    private final FilmRepository filmRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public FilmServiceClient(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    /**
     * Cherche un film par titre :
     * 1. D'abord en base locale
     * 2. Sinon appelle l'API voisins et sauvegarde le film
     */
    public Optional<Film> findOrFetch(String titre) {
        // 1. Base locale
        Optional<Film> local = filmRepository.findByTitre(titre);
        if (local.isPresent()) return local;

        // 2. API voisins
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FILMS_API))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            JsonNode films = mapper.readTree(response.body());

            for (JsonNode f : films) {
                String filmTitle = f.path("title").asText("");
                if (filmTitle.equalsIgnoreCase(titre)) {
                    // Créer et sauvegarder le film en base locale
                    Film film = new Film();
                    film.setTitre(filmTitle);
                    film.setAnneeRealisation(
                            f.path("yearCompletion").asInt(0)
                    );
                    film.setAgeMinimum(
                            f.path("minimumAge").asInt(0)
                    );
                    film.setLouable(
                            f.path("openForRent").asBoolean(false)
                    );
                    return Optional.of(filmRepository.save(film));
                }
            }
        } catch (Exception e) {
            System.err.println("FilmServiceClient error: " + e.getMessage());
        }

        return Optional.empty();
    }
}
