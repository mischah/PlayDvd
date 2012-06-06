package tmdb;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;

import com.github.savvasdalkitsis.jtmdb.CastInfo;
import com.github.savvasdalkitsis.jtmdb.GeneralSettings;
import com.github.savvasdalkitsis.jtmdb.Genre;
import com.github.savvasdalkitsis.jtmdb.Movie;
import com.github.savvasdalkitsis.jtmdb.MovieBackdrop;
import com.github.savvasdalkitsis.jtmdb.MoviePoster;

import forms.DvdForm;
import forms.TmdbInfoForm;

public class InfoGrabber {

  public static List<Movie> searchForMovie(final String movieName) throws GrabberException {

    InfoGrabber.prepareSettings();
    try {
      final List<Movie> search = Movie.search(movieName);
      return search;
    } catch (final IOException e) {
      throw new GrabberException(e);
    } catch (final JSONException e) {
      throw new GrabberException(e);
    }
  }

  /**
   * Gets the movie for the given id
   * 
   * @param tmdbId
   * @return
   * @throws GrabberException
   */
  public static Movie getMovieInfo(final Integer tmdbId) throws GrabberException {
    InfoGrabber.prepareSettings();
    try {
      return Movie.getInfo(tmdbId);
    } catch (final IOException e) {
      throw new GrabberException(e);
    } catch (final JSONException e) {
      throw new GrabberException(e);
    }

  }

  public static DvdForm fillDvdFormWithMovieInfo(final TmdbInfoForm tmdbInfoForm) throws GrabberException {

    final Movie movieInfo = InfoGrabber.getMovieInfo(tmdbInfoForm.movieId);

    final DvdForm dvdForm = new DvdForm();
    dvdForm.title = movieInfo.getName();
    dvdForm.plot = movieInfo.getOverview();
    dvdForm.runtime = movieInfo.getRuntime();

    final Date releasedDate = movieInfo.getReleasedDate();
    final Calendar releaseCal = Calendar.getInstance();
    releaseCal.setTime(releasedDate);
    dvdForm.year = releaseCal.get(Calendar.YEAR);

    final Set<Genre> genres = movieInfo.getGenres();
    for (final Genre genre : genres) {
      dvdForm.genres.add(genre.getName());
    }

    final Set<CastInfo> cast = movieInfo.getCast();
    for (final CastInfo castInfo : cast) {
      if ("Director".equals(castInfo.getJob())) {
        dvdForm.director = castInfo.getName();
        continue;
      }

      if ("Actor".equals(castInfo.getJob())) {
        dvdForm.actors.add(castInfo.getName());

        continue;
      }
    }

    final String tmdbBackDrop = tmdbInfoForm.tmdbBackDrop;
    if (StringUtils.isEmpty(tmdbBackDrop) == false) {
      final Set<MovieBackdrop> backdrops = movieInfo.getImages().backdrops;
      for (final MovieBackdrop movieBackdrop : backdrops) {
        if (movieBackdrop.getID().equals(tmdbBackDrop) == true) {
          dvdForm.backDropUrl = movieBackdrop.getLargestImage().toString();
          break;
        }
      }
    }

    final String tmdbPoster = tmdbInfoForm.tmdbPoster;
    if (StringUtils.isEmpty(tmdbPoster) == false) {
      final Set<MoviePoster> posters = movieInfo.getImages().posters;
      for (final MoviePoster poster : posters) {
        if (poster.getID().equals(tmdbPoster) == true) {
          dvdForm.posterUrl = poster.getLargestImage().toString();
          break;
        }
      }
    }

    return dvdForm;

  }

  private static void prepareSettings() {
    GeneralSettings.setApiKey("a67216a4ad62ec0f81e3fffbfe18507f");
    GeneralSettings.setAPILocale(Locale.GERMANY);
  }

}
