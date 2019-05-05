package it.unimib.disco.abstat.distributed.backend.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.unimib.disco.abstat.distributed.backend.model.Dataset;
import it.unimib.disco.abstat.distributed.backend.model.Ontology;
import it.unimib.disco.abstat.distributed.backend.model.SubmitConfig;


@Service
public class SummarizationServiceImpl implements SummarizationService {

	@Autowired
	DatasetService datasetService;
	@Autowired
	OntologyService ontologyService;
	@Autowired
	SubmitConfigService submitConfigService;
	
	@Autowired
	JavaMailSender emailSender;
	
	@Async("processExecutor")
	public void summarizeAsyncWrapper(SubmitConfig subCfg, String email)   {
		summarize(subCfg, email);
	}
	

	public void summarize(SubmitConfig subCfg, String email) {
		try {
			Dataset dataset = datasetService.findDatasetById(subCfg.getDsId());
			String ontId = subCfg.getListOntId().get(0);
			String ontPath = new File(ontologyService.findOntologyById(ontId).getPath()).getCanonicalPath();

			//spark job submission
			sparkSubmit(dataset.getPath(), ontPath, subCfg.getSummaryPath());
			
			//save configuration
			submitConfigService.add(subCfg);

			
			// mail notification
			if (email != null && !email.equals("")) {
				String text = "Dear user, \n\n" + "A summarization job that you have run on ABSTAT is now completed. \n"
						+ "You can now check and consolidate the summary using the ABSTAT website at http://backend.abstat.disco.unimib.it \n\n"
						+ "The ABSTAT Summarization Framework \n\n" + "\n"
						+ "ABSTAT 1.0 // Licensed under Creative Commons - GNU Affero General Public License v3.010.";
				sendMail(email, text, "OK");
			}
		} catch (Exception e) {
			// mail notification
			if (email != null && !email.equals("")) {
				e.printStackTrace();
				String text = "Dear user, \n\n"
						+ "The summarization job that you have run on ABSTAT has encountered a problem. Please contact support.\n\n"
						+ "The ABSTAT Summarization Framework \n\n" + "\n"
						+ "ABSTAT 1.0 // Licensed under Creative Commons - GNU Affero General Public License v3.010.";
				sendMail(email, text, "error");
			} else {
				e.printStackTrace();
			}
		}
	}	
	
	
	private void sparkSubmit(String dataset, String ontology, String output_dir) throws Exception {
 		String[] cmd = {"/bin/bash","submit-job.sh", dataset, ontology, output_dir};
 		ProcessBuilder pb = new ProcessBuilder(cmd);
 		pb.redirectErrorStream(true);
 		pb.directory(new File(System.getProperty("user.dir")).getParentFile());
 		Process p = pb.start();
 		
 		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
 	    String line = "";
 	    while ((line = reader.readLine()) != null)
 	    	System.out.println(line);
	}
	

	public void sendMail(String email, String text, String status) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		if (status.equals("OK"))
			message.setSubject("Your summary is ready!");
		else
			message.setSubject("Oops!");
		message.setText(text);
		emailSender.send(message);
	}
}
