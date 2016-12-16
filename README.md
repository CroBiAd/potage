# What is POTAGE?

POTAGE (pronounced "[pəʊˈtɑːʒ](http://img2.tfd.com/pron/mp3/en/UK/df/dfskskssdfd5drh7.mp3)") is a web-based tool for integrating genetic map location with gene expression data and inferred functional annotation in wheat.

# POTAGE Web Server

You can access the public POTAGE web server (http://crobia.agwine.adelaide.edu.au/potage) which contains a limited number of published gene
expression data sets.

# Running POTAGE locally

You may be interested in running your own POTAGE server if you:

  1. Need to add additional private/proprietary data sets.
  2. Would prefer not to use our public server (http://crobia.agwine.adelaide.edu.au/potage).

To facilitate this option we have packaged up POTAGE into a Docker image. In order to use this image you will need:

  1. [Install Docker Engine](https://docs.docker.com/engine/installation/) for your operating system.
  2. Run the [crobia/potage](https://hub.docker.com/r/crobia/potage/) image.

For full details about running the `crobia/potage` image, please see the [crobia/potage](https://hub.docker.com/r/crobia/potage/) repository on Docker Hub.