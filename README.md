[![Docker auto](https://img.shields.io/docker/automated/crobiad/potage.svg?logo=docker&style=for-the-badge)](https://hub.docker.com/r/crobiad/potage)
[![Docker build](https://img.shields.io/docker/cloud/build/crobiad/potage.svg?logo=docker&style=for-the-badge)](https://hub.docker.com/r/crobiad/potage)


[![Website](https://img.shields.io/website-up-down-green-red/http/crobiad.agwine.adelaide.edu.au/potage.svg?label=crobiad.agwine.adelaide.edu.au/potage/&style=for-the-badge)](http://crobiad.agwine.adelaide.edu.au/potage/)



# What is POTAGE?

POTAGE (pronounced "[pəʊˈtɑːʒ](http://img2.tfd.com/pron/mp3/en/UK/df/dfskskssdfd5drh7.mp3)") is a web-based tool for integrating genetic map location with gene expression data and inferred functional annotation in wheat.

# POTAGE Web Server

You can access the public POTAGE web server (http://crobiad.agwine.adelaide.edu.au/potage) which contains a limited number of published gene
expression data sets.

  1. [IWGSC RNA-Seq tissue series](https://urgi.versailles.inra.fr/files/RNASeqWheat/)
  2. [Meiose](https://www.ncbi.nlm.nih.gov/bioproject/PRJEB5029)

# Running POTAGE locally

You may be interested in running your own POTAGE server if you:

  1. Need to [add additional unpublished data sets](https://github.com/CroBiAd/potage_data/blob/master/CONTRIBUTING.md#adding-expression-data-sets).
  2. Would prefer not to use our public server (http://crobiad.agwine.adelaide.edu.au/potage).

To facilitate this option we have packaged up POTAGE into a Docker image. In order to use this image you will need to:

  1. [Install Docker Engine](https://docs.docker.com/engine/installation/) for your operating system.
  2. Run the [crobiad/potage](https://hub.docker.com/r/crobiad/potage/) image.

For full details about running the `crobiad/potage` image, please see the [crobiad/potage](https://hub.docker.com/r/crobiad/potage/) repository on Docker Hub.
