#! /bin/bash

set -e

func() {
        wget "$dbpedia_downloads/core-i18n//en/$1.ttl.bz2" -P $triples_directory
        bunzip2 "$triples_directory/$1.ttl.bz2"
        cat $triples_directory/$1.ttl  >> $triples_directory/dataset.nt
        rm $triples_directory/$1.ttl
}

version=$1
include_raw=$2
target_directory=$3/dbpedia-$1$include_raw
dbpedia_downloads="http://downloads.dbpedia.org/${version}"
rm -rf $target_directory
mkdir -p $target_directory

ontology_directory=$target_directory/ontology
mkdir $ontology_directory
wget "$dbpedia_downloads/dbpedia_$version.owl" -P $ontology_directory
triples_directory=$target_directory/triples

func instance_types_en
func mappingbased_literals_en
func mappingbased_objects_en
func specific_mappingbased_properties_en
func persondata_en


if [[ $include_raw == -infobox ]]
then
        func infobox_properties_en
fi
