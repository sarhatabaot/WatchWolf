# Base: cgr.dev/chainguard/wolfi-base:latest
# Pinned: 2026-11-21
# Note: The digest below is for reference only. Chainguard frequently rebuilds Wolfi;
#       to obtain the latest digest, run:
#       docker pull cgr.dev/chainguard/wolfi-base:latest
#       docker image inspect cgr.dev/chainguard/wolfi-base:latest --format '{{index .RepoDigests 0}}'
FROM cgr.dev/chainguard/wolfi-base@sha256:be019c1c74fe69204a42c4e10bc4d886a49bf5403ffa74374ef7baa408fc9a26

ENV PYTHONUNBUFFERED=1
WORKDIR /app

# Install Python, pip, Node.js, and npm
RUN apk add --no-cache \
      python3 \
      py3-pip \
      nodejs \
      npm \
    && rm -rf /var/cache/apk/*

# Install Python and Node dependencies, then clean caches
RUN pip3 install --no-cache-dir javascript==1!1.0.0 pathlib \
    && npm install --save bytefield-svg \
    && npm cache clean --force

COPY api-docs.py api-docs.py

CMD [ "python3", "api-docs.py"]