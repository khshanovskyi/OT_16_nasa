package ua.khshanovskyi.ot_16_nasa.service;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.khshanovskyi.ot_16_nasa.entity.Photo;
import ua.khshanovskyi.ot_16_nasa.entity.Photos;
import ua.khshanovskyi.ot_16_nasa.exception.NoUriException;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
public class PictureService {

    @Cacheable("maxImageUrl")
    public URI getUriWithMaxPictureSize(URI uri) throws NoUriException {
        return getPhotos(uri).parallelStream()
                .map(Photo::getUrl)
                .map(PictureService::getUriToSize)
                .max(Comparator.comparing(Pair::getRight))
                .filter(pair -> pair.getRight() != -1)
                .map(Pair::getLeft)
                .orElseThrow(NoUriException::new);
    }

    private List<Photo> getPhotos(URI uri) {
        return Optional.ofNullable(new RestTemplate().getForObject(uri, Photos.class))
                .map(Photos::getPhotos)
                .orElse(Collections.emptyList());
    }

    @SneakyThrows
    private static Pair<URI, Long> getUriToSize(String url) {
        URI uri = URI.create(url);
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);

        String contentLength = Optional.ofNullable(restTemplate.headForHeaders(uri).get(HttpHeaders.CONTENT_LENGTH))
                .map(strings -> strings.get(0))
                .orElse("-1");

        return Pair.of(uri, Long.parseLong(contentLength));
    }
}
