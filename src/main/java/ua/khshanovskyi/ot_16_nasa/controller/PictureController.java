package ua.khshanovskyi.ot_16_nasa.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.khshanovskyi.ot_16_nasa.exception.NoUriException;
import ua.khshanovskyi.ot_16_nasa.service.PictureService;
import ua.khshanovskyi.ot_16_nasa.service.UriConfigurationService;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/pictures")
@AllArgsConstructor
@Log4j2
public class PictureController {
    private static final String HOST = "https://api.nasa.gov";
    private static final String PATH = "/mars-photos/api/v1/rovers/curiosity/photos";
    private static final String KEY = "A8TWOxIOLYey639GppGaUsthUE3etmlDlYr1MfbS";

    private PictureService pictureService;
    private UriConfigurationService uriConfigurationService;

    @GetMapping("/{sol}/largest")
    public ResponseEntity<URI> getMaxPictureBySol(@PathVariable int sol){
        URI uriWithMaxPictureSize;
        try {
            uriWithMaxPictureSize = pictureService.getUriWithMaxPictureSize(uriConfigurationService.generateUri(HOST, PATH, getParametersMap(sol)));
        } catch (NoUriException exception) {
            log.error(exception);
            return ResponseEntity
                    .status(HttpStatus.PERMANENT_REDIRECT)
                    .location(uriConfigurationService.generateUri(HOST))
                    .build();
        }

        return ResponseEntity
                .status(HttpStatus.PERMANENT_REDIRECT)
                .location(uriWithMaxPictureSize)
                .build();
    }

    private Map<String, ?> getParametersMap(int sol){
        Map<String, Object> parametersMap = new LinkedHashMap<>();
        parametersMap.put("sol", sol);
        parametersMap.put("api_key", KEY);
        return parametersMap;
    }
}
