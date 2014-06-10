---
title: 
layout: default
---

# deeplearning4j vs. torch7 vs. pylearn2

Deeplearning4j is not the first open-source deep-learning project, but it is distinguished from its predecessors in both programming language and intent. 

Most academic researchers working on deep learning relies on Pylearn2 and Theano, which are written in Python, an interpreted language. Pylearn2 is a machine-learning library, while Theano is a library that handles multidimensional arrays. Both are powerful tools widely used for research purposes and serving a large community. They are well suited to data exploration. 

Torch7 is a computational framework written in Lua that supports machine-learning algorithms. It is purported to be used on large tech companies that devote teams to deep learning. Lua is a multi-paradigm language developed in Brazil in the early 1990s. 

While powerful, Torch7 was not designed to be widely accessible to either the Python-based academic community, nor to corporate software engineers, whose lingua franca is Java. 

The 'j' in Deeplearning4j stands for Java. We chose Java to reflect our focus on industry and ease of use. While both Torch7 and DL4J employ parallelism, DL4J's parallelism is automatic. That is, we automate the setting up of worker nodes and connections, allowing users to bypass libs while creating a massively parallel network. Deeplearning4j is best suited for solving specific problems, and doing so quickly. 