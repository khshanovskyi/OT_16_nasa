package ua.khshanovskyi.ot_16_nasa.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
public class UriConfigurationService {

    public URI generateUri(String url){
        Objects.requireNonNull(url);
        return UriComponentsBuilder.fromUriString(url).build().toUri();
    }

    public URI generateUri(String url, String path, Map<String, ?> parameters) {
        Objects.requireNonNull(url);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url).path(path);
        parameters.forEach(uriBuilder::queryParam);

        return uriBuilder.build().toUri();
    }



}
