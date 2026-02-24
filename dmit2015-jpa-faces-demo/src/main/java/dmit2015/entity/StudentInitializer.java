package dmit2015.entity;

import dmit2015.repository.StudentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StudentInitializer {

//    @PersistenceContext
//    private EntityManager entityManager;

    @Inject
    private StudentRepository studentRepository;

    @Transactional
    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        if (studentRepository.count() == 0) {
            var student1 = new Student();
            student1.setFirstName("Lance");
            student1.setLastName("Beuno");
            studentRepository.add(student1);

            var student2 = new Student();
            student2.setFirstName("Katie");
            student2.setLastName("Hladun");
            studentRepository.add(student2);

            var student3 = new Student();
            student3.setFirstName("Praise");
            student3.setLastName("Rebi John");
            studentRepository.add(student3);
        }
    }

}
