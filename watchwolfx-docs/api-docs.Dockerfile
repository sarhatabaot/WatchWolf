FROM cgr.dev/chainguard/wolfi-base:latest

ENV PYTHONUNBUFFERED=1
ENV UV_PYTHON=/usr/bin/python3.12
WORKDIR /app

RUN apk add --no-cache \
      nodejs-22 \
      npm \
      python-3.12 \
      uv

COPY api-docs-requirements.txt ./
RUN uv venv \
    && uv pip install --python .venv/bin/python --requirement api-docs-requirements.txt

COPY node/package.json node/package-lock.json /tmp/watchwolfx-node/
RUN bridge_dir=/app/.venv/lib/python3.12/site-packages/javascript/js \
    && cp /tmp/watchwolfx-node/package.json "$bridge_dir/package.json" \
    && cp /tmp/watchwolfx-node/package-lock.json "$bridge_dir/package-lock.json" \
    && npm ci --omit=dev --prefix "$bridge_dir" \
    && npm cache clean --force \
    && rm -rf /tmp/watchwolfx-node

COPY api-docs.py api-docs.py

CMD [".venv/bin/python", "api-docs.py"]
