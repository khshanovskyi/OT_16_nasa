package ua.khshanovskyi.ot_16_nasa.service;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
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
import ua.khshanovskyi.ot_16_nasa.exception.NoSoleException;
import ua.khshanovskyi.ot_16_nasa.exception.NoUriException;

import java.net.URI;
import java.util.*;

@Service
@NoArgsConstructor
public class PictureService {

    @Cacheable("maxImageUrl")
    public URI getUrlWithMaxPictureSize(String host, String path, int sol, String key) throws NoSoleException, NoUriException {
        return getPhotos(generateUri(host, path, sol, key)).parallelStream()
                .map(Photo::getUrl)
                .map(PictureService::getUriToSize)
                .max(Comparator.comparing(Pair::getRight))
                .filter(pair -> pair.getRight() != -1)
                .map(Pair::getLeft)
                .orElseThrow(NoUriException::new);
    }

    private URI generateUri(String host, String path, int sol, String key) throws NoSoleException {
        if (sol == 0) throw new NoSoleException("The sol is " + sol);
        return Objects.nonNull(key) && StringUtils.isNotEmpty(key) ?
                URI.create(host + path + "?sol=" + sol + "&api_key=" + key) :
                URI.create(host + path + "?sol=" + sol + "&api_key=DEMO_KEY");
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
