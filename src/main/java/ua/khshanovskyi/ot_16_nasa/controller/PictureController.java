package ua.khshanovskyi.ot_16_nasa.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ua.khshanovskyi.ot_16_nasa.exception.NoSoleException;
import ua.khshanovskyi.ot_16_nasa.exception.NoUriException;
import ua.khshanovskyi.ot_16_nasa.service.PictureService;

import java.net.URI;

@RestController
@RequestMapping("/pictures")
@AllArgsConstructor
@Log4j2
public class PictureController {
    private PictureService pictureService;

    @GetMapping("/{sol}/largest")
    public ModelAndView getMaxPictureBySol(@PathVariable int sol){
        URI uri = null;
        try {
            uri = pictureService.getUrlWithMaxPictureSize("https://api.nasa.gov",
                    "/mars-photos/api/v1/rovers/curiosity/photos", sol, "A8TWOxIOLYey639GppGaUsthUE3etmlDlYr1MfbS");
        } catch (NoSoleException | NoUriException e) {
            e.printStackTrace();
            return new ModelAndView("redirect:" + "https://api.nasa.gov/");
        }
        return new ModelAndView("redirect:" + uri);
    }
}
