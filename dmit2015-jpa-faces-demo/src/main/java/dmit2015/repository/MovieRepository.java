package dmit2015.repository;

import dmit2015.entity.Movie;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.SecurityContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MovieRepository {

    @Inject
    private SecurityContext securityContext;
    
    @PersistenceContext //(unitName = "h2-dmit2015-jpa-pu") // unitName is optional if persistence.xml contains only one persistence-unit
    private EntityManager em;

    private void requiresAuthentication() {
        String username = securityContext.getCallerPrincipal().getName();
        if (username.equalsIgnoreCase("anonymous")) {
            throw new RuntimeException("Access denied. You must login first.");
        }
    }

    private void requiresSalesRole() {
        String username = securityContext.getCallerPrincipal().getName();
        if (!username.equalsIgnoreCase("Sales")) {
            throw new RuntimeException("Access denied. Your role does not have permission to perfrom this operation");
        }
    }


    @Transactional
    public void add(Movie newMovie) {
        requiresAuthentication();
        requiresSalesRole();
        String username = securityContext.getCallerPrincipal().getName();
        newMovie.setUsername(username);

        em.persist(newMovie);
    }

    @Transactional
    public void update(Movie updatedMovie) {
        requiresAuthentication();
        requiresSalesRole();
        Optional<Movie> maybeMovie = findOptionalById(updatedMovie.getId());
        if (maybeMovie.isPresent()) {
            Movie existingMovie = maybeMovie.orElseThrow();
            existingMovie.setTitle(updatedMovie.getTitle());
            existingMovie.setGenre(updatedMovie.getGenre());
            existingMovie.setPrice(updatedMovie.getPrice());
            existingMovie.setRating(updatedMovie.getRating());
            existingMovie.setReleaseDate(updatedMovie.getReleaseDate());
            em.merge(existingMovie);
        }
    }

    @Transactional
    public void delete(Movie existingMovie) {
        requiresAuthentication();
        if (!em.contains(existingMovie)) {
            existingMovie = em.merge(existingMovie);
        }
        em.remove(existingMovie);
    }

    @Transactional
    public void deleteById(Long id) {
        requiresAuthentication();
        Optional<Movie> maybeMovie = findOptionalById(id);
        if (maybeMovie.isPresent()) {
            Movie existingMovie = maybeMovie.orElseThrow();
            em.remove(existingMovie);
        }
    }

    public Movie findById(Long id) {
        requiresAuthentication();
        return em.find(Movie.class, id);
    }

    public Optional<Movie> findOptionalById(Long id) {
        requiresAuthentication();
        try {
            Movie querySingleResult = findById(id);
            return Optional.of(querySingleResult);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public List<Movie> findAll() {
        /** Deny anonymous user access to this method.
         * Only Shipping or Sales role are allowed.
         * The Shipping role has access to all movies.
         * The Sales roles have only access to movies created by them.
         * */
        // Deny anonymous user access to this method.
        requiresAuthentication();
        // Only Shipping or Sales role are allowed.
        boolean hasShippingOrSalesRole = securityContext.isCallerInRole("Shipping")
                || securityContext.isCallerInRole("Sales");
        if (!hasShippingOrSalesRole) {
            throw new RuntimeException("Access denied. Your roles does not allow to perform this operation.");
        }
        // The Shipping role has access to all movies.
        boolean hasShippingRole = securityContext.isCallerInRole("Shipping");
        if (hasShippingRole) {
            return em.createQuery("SELECT m FROM Movie m ", Movie.class)
                    .getResultList();
        }
        // The Sales roles have only access to movies created by them.
        String username = securityContext.getCallerPrincipal().getName();
        return em.createQuery("SELECT m FROM Movie m WHERE m.username = :uname", Movie.class)
                .setParameter("uname", username)
                .getResultList();
    }

    public List<Movie> findAllOrderByTitle() {
        return em.createQuery("SELECT m FROM Movie m ORDER BY m.title", Movie.class)
                .getResultList();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(m) FROM Movie m", Long.class).getSingleResult().longValue();
    }

    @Transactional
    public void deleteAll() {
        em.createQuery("DELETE FROM Movie").executeUpdate();
    }

}

