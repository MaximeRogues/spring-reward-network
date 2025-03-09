package accounts.client;

import common.money.Percentage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rewards.internal.account.Account;
import rewards.internal.account.Beneficiary;

import java.net.URI;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class AccountClientTests {

	private static final String BASE_URL = "http://localhost:8080";
	
	private RestTemplate restTemplate = new RestTemplate();
	private Random random = new Random();

    @Test
	public void listAccounts() {
		Account[] accounts = restTemplate.getForObject(BASE_URL + "/accounts", Account[].class);
		
		assertNotNull(accounts);
		assertTrue(accounts.length >= 21);
		assertEquals("Keith and Keri Donald", accounts[0].getName());
		assertEquals(2, accounts[0].getBeneficiaries().size());
		assertEquals(Percentage.valueOf("50%"), accounts[0].getBeneficiary("Annabelle").getAllocationPercentage());
	}
	
	@Test
	public void getAccount() {
		Account account = restTemplate.getForObject(BASE_URL + "/accounts/0", Account.class);
		
		assertNotNull(account);
		assertEquals("Keith and Keri Donald", account.getName());
		assertEquals(2, account.getBeneficiaries().size());
		assertEquals(Percentage.valueOf("50%"), account.getBeneficiary("Annabelle").getAllocationPercentage());
	}
	
	@Test
	public void createAccount() {
		// Use a unique number to avoid conflicts
		String number = String.format("12345%4d", random.nextInt(10000));
		Account account = new Account(number, "John Doe");
		account.addBeneficiary("Jane Doe");

		// on récupère la Location du nouvel Account créé
		URI newAccountLocation = restTemplate.postForLocation(BASE_URL + "/accounts", account);

		// on vérifie qu'on arrive bien à récupérer ce nouvel Account
		assertNotNull(newAccountLocation);
		Account retrievedAccount = restTemplate.getForObject(newAccountLocation, Account.class);
		
		assertEquals(account.getNumber(), retrievedAccount.getNumber());
		
		Beneficiary accountBeneficiary = account.getBeneficiaries().iterator().next();
		Beneficiary retrievedAccountBeneficiary = retrievedAccount.getBeneficiaries().iterator().next();
		
		assertEquals(accountBeneficiary.getName(), retrievedAccountBeneficiary.getName());
		assertNotNull(retrievedAccount.getEntityId());
	}
	
	@Test
	public void addAndDeleteBeneficiary() {
		// on va ajouter puis supprimer un nouveau bénéficiaire
		final String BENEFICIARY_NAME = "David";
		// on récupère la Location du nouveau bénéficiaire créé
        URI location = restTemplate.postForLocation(BASE_URL + "/accounts/{accountId}/beneficiaries", BENEFICIARY_NAME, 1);
		
		assertNotNull(location);
		// on récupère le nouveau bénéficiaire créé
		Beneficiary newBeneficiary = restTemplate.getForObject(location, Beneficiary.class);
		
		assertNotNull(newBeneficiary);
		// on vérifie que son nom est bien celui qu'on a envoyé comme paramètre
		assertEquals(BENEFICIARY_NAME, newBeneficiary.getName());
		
		restTemplate.delete(location);

		HttpClientErrorException httpClientErrorException = assertThrows(HttpClientErrorException.class, () -> {
			System.out.println("You SHOULD get the exception \"No such beneficiary with name 'David'\" in the server.");

			// on va essayer de récupérer le bénéficiaire qu'on vient de supprimer pour s'assurer qu'une exception est levée
			restTemplate.getForObject(location, Beneficiary.class);
		});
		assertEquals(HttpStatus.NOT_FOUND, httpClientErrorException.getStatusCode());
	}
	
}
