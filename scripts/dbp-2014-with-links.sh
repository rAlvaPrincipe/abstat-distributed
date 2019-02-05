#! /bin/bash

set -e

func() {
        wget "$dbpedia_downloads/en/$1.nt.bz2" -P $triples_directory
        bunzip2 "$triples_directory/$1.nt.bz2"
        cat $triples_directory/$1.nt  >> $triples_directory/dataset.nt
        rm $triples_directory/$1.nt
}

links() {
	wget "$dbpedia_downloads/links/$1.nt.bz2" -P $triples_directory
        bunzip2 "$triples_directory/$1.nt.bz2"
        cat $triples_directory/$1.nt  >> $triples_directory/dataset.nt
        rm $triples_directory/$1.nt
}

version=2014
target_directory=$1/dbpedia-2014-with-links
dbpedia_downloads="http://downloads.dbpedia.org/${version}"
rm -rf $target_directory
mkdir -p $target_directory

ontology_directory=$target_directory/ontology
mkdir $ontology_directory
wget "$dbpedia_downloads/dbpedia_$version.owl" -P $ontology_directory

triples_directory=$target_directory/triples


func article_categories_en
func article_templates_en
func category_labels_en  
func disambiguations_en
func disambiguations_unredirected_en
func external_links_en
func flickr_wrappr_links_en
func freebase_links_en
func genders_en
func geo_coordinates_en
func geonames_links_en_en
func homepages_en
func images_en
func infobox_properties_en
func infobox_properties_unredirected_en
func infobox_property_definitions_en
func infobox_test_en
func instance_types_en
func instance_types_heuristic_en
func interlanguage_links_chapters_en
func interlanguage_links_en
func iri_same_as_uri_en
func labels_en
func long_abstracts_en
func mappingbased_properties_cleaned_en
func mappingbased_properties_en
func mappingbased_properties_unredirected_en
func old_interlanguage_links_en
func out_degree_en
func page_ids_en
func page_length_en
func page_links_en
func page_links_unredirected_en
func persondata_en
func persondata_unredirected_en
func pnd_en
func redirects_en
func redirects_transitive_en
func revision_ids_en
func revision_uris_en
func short_abstracts_en
func skos_categories_en
func specific_mappingbased_properties_en
func topical_concepts_en
func topical_concepts_unredirected_en
func wikipedia_links_en

links amsterdammuseum_links
links bbcwildlife_links
links bookmashup_links
links bricklink_links
links cordis_links
links dailymed_links
links dblp_links
links dbtune_links
links diseasome_links
links drugbank_links
links eunis_links
links eurostat_linkedstatistics_links
links eurostat_wbsg_links
links factbook_links
links flickrwrappr_links
links gadm_links
links geospecies_links
links gho_links
links gutenberg_links
links italian_public_schools_links
links linkedgeodata_links
links linkedmdb_links
links musicbrainz_links
links nytimes_links
links opencyc_links
links openei_links
links revyu_links
links sider_links
links tcm_links
links umbel_links
links uscensus_links
links wikicompany_links
links wordnet_links
links yago_links
links yago_taxonomy
links yago_type_links
links yago_types

