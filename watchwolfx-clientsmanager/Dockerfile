FROM cgr.dev/chainguard/wolfi-base:latest

ENV PYTHONUNBUFFERED=1
ENV UV_PYTHON=/usr/bin/python3.12
#ENV NODE_OPTIONS="--unhandled-rejections=strict --trace-warnings"
#ENV DEBUG="jspybridge"
WORKDIR /app

RUN apk add --no-cache \
        nodejs-22 \
        npm \
        python-3.12 \
        uv \
        xvfb-run

COPY pyproject.toml uv.lock ./
RUN uv sync --frozen --no-dev --no-install-project

COPY node/package.json node/package-lock.json /tmp/watchwolfx-node/
RUN bridge_dir=/app/.venv/lib/python3.12/site-packages/javascript/js \
    && cp /tmp/watchwolfx-node/package.json "$bridge_dir/package.json" \
    && cp /tmp/watchwolfx-node/package-lock.json "$bridge_dir/package-lock.json" \
    && npm ci --omit=dev --prefix "$bridge_dir" \
    && npm cache clean --force \
    && rm -rf /tmp/watchwolfx-node

COPY . .
RUN uv sync --frozen --no-dev \
    && mkdir -p /app/logs \
    && chown -R nonroot:nonroot /app/logs

EXPOSE 7000-7199

USER nonroot
CMD ["xvfb-run", "--auto-servernum", "--server-num=1", "--server-args=-ac -screen 0 1280x1024x24", "uv", "run", "--no-sync", "watchwolfx-clientsmanager"]
