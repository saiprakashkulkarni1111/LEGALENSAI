"""Authentication API for analyst sessions."""
from fastapi import APIRouter, HTTPException, status
from pydantic import BaseModel, Field
from backend.app.security.auth import create_access_token

router = APIRouter(prefix="/api/auth", tags=["Authentication"])

DEMO_USERS = {
    "analyst@legalens.ai": {
        "password": "analyst-demo",
        "role": "analyst",
        "sub": "user_analyst_01",
        "organization": "Independent Practice",
    }
}


class LoginRequest(BaseModel):
    email: str = Field(..., examples=["analyst@legalens.ai"])
    password: str


class LoginResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    email: str
    role: str
    notice: str = "Legal information, not legal advice. AI can make mistakes."


@router.post("/login", response_model=LoginResponse)
async def login(payload: LoginRequest):
    user = DEMO_USERS.get(payload.email.lower())
    if not user or payload.password != user["password"]:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid email or password.",
        )
    token = create_access_token(
        {
            "sub": user["sub"],
            "email": payload.email.lower(),
            "role": user["role"],
            "organization": user["organization"],
        }
    )
    return LoginResponse(
        access_token=token,
        email=payload.email.lower(),
        role=user["role"],
    )
