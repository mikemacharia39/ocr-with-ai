FROM ubuntu:25.10

# Install necessary dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    git cmake g++ wget libstdc++6 libopenblas-dev build-essential make gcc libcurl4-openssl-dev && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Clone llama.cpp repository
RUN GIT_SSL_NO_VERIFY=true git clone https://github.com/ggml-org/llama.cpp.git

WORKDIR /app/llama.cpp

# Build llama.cpp
RUN mkdir build && cd build && cmake .. && make -j$(nproc)

# Change into the directory containing the built binary
WORKDIR /app/llama.cpp/build/bin

# docker run -it --rm llama_cpp_custom_image:1.0 /bin/bash <- good for troubleshooting
# Set entrypoint
CMD ["./llama-server", "--host", "0.0.0.0", "--port", "8080", "-m", "/models/gemma-3-4b-it-q4_0.gguf"]