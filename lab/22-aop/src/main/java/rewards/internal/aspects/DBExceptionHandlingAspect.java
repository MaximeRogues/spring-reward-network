package rewards.internal.aspects;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import rewards.internal.exception.RewardDataAccessException;


@Aspect
@Component
public class DBExceptionHandlingAspect {
	
	public static final String EMAIL_FAILURE_MSG = "Failed sending an email to Mister Smith : ";
	
	private Logger logger = LoggerFactory.getLogger(getClass());


	// Logs qui seront affichés après qu'une méthode d'un Repository a échoué
	@AfterThrowing(value = "execution(* rewards.internal.*.*Repository.*(..)),", throwing = "exception")
	public void implExceptionHandling(RewardDataAccessException exception) {
		// Log a failure warning
		logger.warn(EMAIL_FAILURE_MSG + exception + "\n");
	}
}
