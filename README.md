# ocr-with-ai

I am going to explore different ideas and ai concepts in this project, that are not directly related to
the main project, but are still interesting and useful.

## Model to use

- We are going to use Ollama, a tool that allows you to run large language models locally on your machine.

### Ollama

Ollama is a tool that allows you to run large language models locally on your machine.

````
C:\Users\MikeHenry>ollama serve
Error: listen tcp 127.0.0.1:11434: bind: Only one usage of each socket address (protocol/network address/port) is normally permitted.
````

This `127.0.0.1:11434` is already autoconfigured in the Ollama api class
![ollama-api.png](ollama-api.png)


### Llama.cpp
Llama.cpp is a C++ library for running large language models, such as LLaMA, on your local machine. It is more \
powerful than Ollama, as it allows you to run models with more parameters and more efficiently. \
It is designed to be fast and efficient, allowing you to run models with low latency and high throughput. For our \
purpose, we shall run it in a docker container. We shall also download the models from hugging face \

https://github.com/ggml-org/llama.cpp/pkgs/container/llama.cpp \

https://en.wikipedia.org/wiki/Llama.cpp \

We then download the models from hugging face and place them in the `models` folder \
https://huggingface.co/models

We can only download the models that are compatible with the llama.cpp library, in this with the `ggml` format. \
https://github.com/ggml-org/ggml/blob/master/docs/gguf.md

Instructions for self hosting LLMS in production with llama.cpp llama service \
https://docs.servicestack.net/ai-server/llama-server#hosting-llama-server-with-docker
