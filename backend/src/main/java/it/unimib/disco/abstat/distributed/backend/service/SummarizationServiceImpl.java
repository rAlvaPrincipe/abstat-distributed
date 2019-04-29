package it.unimib.disco.abstat.distributed.backend.service;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.unimib.disco.abstat.distributed.backend.model.Dataset;
import it.unimib.disco.abstat.distributed.backend.model.SubmitConfig;



@Service
public class SummarizationServiceImpl implements SummarizationService {

	private static final int nCores = Runtime.getRuntime().availableProcessors();
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
			String dsId = subCfg.getDsId();
			Dataset dataset = datasetService.findDatasetById(dsId);
			String datasetName = dataset.getName();
			String ontId = subCfg.getListOntId().get(0);
			String ontPath = new File(ontologyService.findOntologyById(ontId).getPath()).getCanonicalPath();
			String summary_dir = subCfg.getSummaryPath();

			String datasetSupportFileDirectory = summary_dir + "/reports/tmp-data-for-computation/";
			checkFile(summary_dir);
			checkFile(datasetSupportFileDirectory);

			//spark algorithm
			script();
			
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
	
	private void script() throws Exception {
 		String[] cmd = {"../submit-job.sh"};
 		Process p = Runtime.getRuntime().exec(cmd);
 		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
 	    String line = "";
 	    while ((line = reader.readLine()) != null)
 	    	System.out.println(line);
	}
	

	private void checkFile(String path_dir) throws Exception {
		File dir = new File(path_dir);

		if (dir.exists())
			FileUtils.deleteDirectory(dir);
		if (dir.mkdirs())
			System.out.println("Successfully created:" + dir);
		else
			System.out.println("Failed to create: " + dir);
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
