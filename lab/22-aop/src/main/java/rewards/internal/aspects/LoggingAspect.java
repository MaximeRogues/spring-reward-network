package rewards.internal.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import rewards.internal.monitor.Monitor;
import rewards.internal.monitor.MonitorFactory;

@Aspect
@Component
public class LoggingAspect {
    public final static String BEFORE = "'Before'";
    public final static String AROUND = "'Around'";

	private Logger logger = LoggerFactory.getLogger(getClass());
	private MonitorFactory monitorFactory;

	
	public LoggingAspect(MonitorFactory monitorFactory) {
		super();
		this.monitorFactory = monitorFactory;
	}


	// Logs qui seront affichés avant qu'une méthode commençant par "find" d'un Repository
	// ne soit exécutée
	@Before("execution(* rewards.internal.*.*Repository.find*(..))")
	public void implLogging(JoinPoint joinPoint) {
		// Do not modify this log message or the test will fail
		logger.info(BEFORE + " advice implementation - " + joinPoint.getTarget().getClass() + //
				"; Executing before " + joinPoint.getSignature().getName() + //
				"() method");
	}


	// Logs qui seront affichés au moment où une méthode commençant par "update" d'un Repository
	// est exécutée
	@Around("execution(* rewards.internal.*.*Repository.update*(..))")
	public Object monitor(ProceedingJoinPoint repositoryMethod) throws Throwable {
		String name = createJoinPointTraceName(repositoryMethod);
		Monitor monitor = monitorFactory.start(name);
		try {
			return repositoryMethod.proceed();
		} finally {
			monitor.stop();
			// Do not modify this log message or the test will fail
			logger.info(AROUND + " advice implementation - " + monitor);
		}
	}
		
	private String createJoinPointTraceName(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		StringBuilder sb = new StringBuilder();
		sb.append(signature.getDeclaringType().getSimpleName());
		sb.append('.').append(signature.getName());
		return sb.toString();
	} 
}