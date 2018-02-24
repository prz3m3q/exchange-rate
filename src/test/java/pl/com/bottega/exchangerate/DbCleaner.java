package pl.com.bottega.exchangerate;

import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Component
public class DbCleaner {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void clean() {
		entityManager.createNativeQuery("DELETE FROM rate").executeUpdate();
		entityManager.createNativeQuery("TRUNCATE rate").executeUpdate();
	}

}
