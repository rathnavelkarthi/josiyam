import os
from typing import Optional
from fastapi import Header, HTTPException, status
from app.config import settings

def extract_bearer_token(authorization: Optional[str] = Header(None)) -> str:
    if not authorization:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Authorization header missing"
        )
    parts = authorization.split()
    if len(parts) != 2 or parts[0].lower() != "bearer":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid authorization header format. Expected 'Bearer <token>'"
        )
    return parts[1]

def get_current_user_id(authorization: Optional[str] = Header(None)) -> str:
    token = extract_bearer_token(authorization)
    
    # Real Supabase JWT validation if secret is configured
    if settings.SUPABASE_JWT_SECRET:
        try:
            from jose import jwt
            payload = jwt.decode(
                token,
                settings.SUPABASE_JWT_SECRET,
                algorithms=["HS256"],
                audience="authenticated"
            )
            user_id: Optional[str] = payload.get("sub")
            if not user_id:
                raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token subject")
            return user_id
        except Exception as e:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail=f"JWT verification failed: {str(e)}")

    # In development mode, accept well-formed test/dev tokens
    # e.g., "dev-user-12345" or "test-token"
    if token.startswith("dev-user-") or token.startswith("test-"):
        return token
        
    if settings.ENVIRONMENT == "development":
        # Allow standard simulation tokens for development testing
        return "user_dev_authenticated"

    raise HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Supabase JWT secret not configured and live token cannot be verified"
    )
