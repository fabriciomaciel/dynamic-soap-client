package br.com.maciel.dynamicsoapclient;

import br.com.maciel.dynamicsoapclient.service.DynamicSoapClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DynamicSoapClientApplication implements CommandLineRunner {

	private final String WSDL_URL = "http://www.dneonline.com/calculator.asmx";
	@Autowired
	private DynamicSoapClientService service;


	public static void main(String[] args) {
		SpringApplication.run(DynamicSoapClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {


		/**
		 * obtem os dados do webservice pelo seu WSDL
		 */
		service.parseWsdlFile(WSDL_URL);
		/**
		 * Executa uma chamada ao webservice
		 */
		service.obterDadosWebService(WSDL_URL);

	}
}
