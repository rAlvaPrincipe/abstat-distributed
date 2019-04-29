package it.unimib.disco.abstat.distributed.backend.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.unimib.disco.abstat.distributed.backend.model.SubmitConfig;
import it.unimib.disco.abstat.distributed.backend.service.DatasetService;
import it.unimib.disco.abstat.distributed.backend.service.OntologyService;
import it.unimib.disco.abstat.distributed.backend.service.SummarizationService;


@CrossOrigin(origins = "*")
@Controller
public class SummarizatorAPI {
	
	@Autowired
	DatasetService datasetService;
	@Autowired
	OntologyService ontologyService;
	@Autowired
	SummarizationService summarizationServ;

							
	@RequestMapping(value = "/summarizator", method = RequestMethod.POST)
	@ResponseBody
	public Callable<String> SummrizatorAPI(@RequestParam(value="dataset", required=true) String datasetId,
			@RequestParam(value="ontologies", required=false, defaultValue="empty_ontology") String ontologyIdList,
			@RequestParam(value="concept_min", required=false, defaultValue="false") Boolean conceptMin,
			@RequestParam(value="inference", required=false, defaultValue="false") Boolean inference,
			@RequestParam(value="cardinality", required=false, defaultValue="false") Boolean cardinality,
			@RequestParam(value="rich_cardinalities", required=false, defaultValue="false") Boolean richCardinalities,
			@RequestParam(value="property_min", required=false, defaultValue="false") Boolean propertyMin,
			@RequestParam(value="email", required=false) String email,
			@RequestParam(value="async", required=false, defaultValue="true") Boolean async) throws Exception{

		//create ontology ids list
		List<String> listOntId = new ArrayList<String>();
		for(String ontologyId : ontologyIdList.split(","))
			listOntId.add(ontologyId);
		
		//create SubmitConfig object
		SubmitConfig config = new SubmitConfig();
		config.setDsId(datasetId);
		config.setListOntId(listOntId);
		config.setTipoMinimo(conceptMin);
		config.setInferences(inference);
		config.setCardinalita(cardinality);
		config.setRichCardinalities(richCardinalities);
		config.setPropertyMinimaliz(propertyMin);
		
		//build output file name
		String inf = ""; String minTp = ""; String propMin = ""; String card = "";
		if(conceptMin)  minTp = "MinTp";
		if(inference)   inf = "Inf";
		if(cardinality) card = "Card";
		if(propertyMin) propMin = "PropMin";
		String datasetName =  datasetService.findDatasetById(config.getDsId()).getName();
		String ontName = ontologyService.findOntologyById(config.getListOntId().get(0)).getName();
		
		String summary_dir = "../data/summaries/" + datasetName + "_" + ontName + "_" + minTp + propMin + card + inf +"/";
		
		//add new attributes to config
		ArrayList<String> ontlogiesListName = new ArrayList<String>();
		for(String id : config.getListOntId())
			ontlogiesListName.add(ontologyService.findOntologyById(id).getName());
		
		Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy hh:mm:ss");
		
        config.setDsName(datasetName);
        config.setListOntNames(ontlogiesListName);
        config.setSummaryPath(summary_dir);
        config.setTimestamp(ft.format(dNow));
		
        Callable<String> callable = null;
		if(async) {
			//run async summarization
	        callable = new Callable<String>() {
				@Override
	            public String call () throws Exception {
					summarizationServ.summarizeAsyncWrapper(config, email);
					return "request submitted";
				}
			};
		}
		else 
			summarizationServ.summarize(config, email);
	
		
		return callable;
	}
}
