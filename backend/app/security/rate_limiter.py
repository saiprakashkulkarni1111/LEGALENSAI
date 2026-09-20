"""
LEGALENS AI - Rate Limiter
In-memory sliding window rate limiter preventing API abuse and DoS vectors.
"""
import time
from collections import defaultdict
from typing import Dict, List
from fastapi import Request, HTTPException, status
from backend.app.config import settings


class InMemoryRateLimiter:
    def __init__(self, requests_per_minute: int = 120):
        self.requests_per_minute = requests_per_minute
        self.client_history: Dict[str, List[float]] = defaultdict(list)

    async def check_rate_limit(self, request: Request):
        client_ip = request.client.host if request.client else "127.0.0.1"
        now = time.time()
        window_start = now - 60.0

        # Purge timestamps outside the 60-second window
        timestamps = [t for t in self.client_history[client_ip] if t > window_start]
        self.client_history[client_ip] = timestamps

        if len(timestamps) >= self.requests_per_minute:
            raise HTTPException(
                status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                detail=f"Rate limit exceeded: Max {self.requests_per_minute} requests per minute allowed."
            )

        self.client_history[client_ip].append(now)


rate_limiter = InMemoryRateLimiter(requests_per_minute=settings.RATE_LIMIT_PER_MINUTE)
