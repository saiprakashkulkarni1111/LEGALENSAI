"""
LEGALENS AI - Base Agent Interface
Defines the contract for specialized, typed AI agents communicating via Pydantic models.
"""
from abc import ABC, abstractmethod
from typing import Dict, Any


class BaseAgent(ABC):
    @property
    @abstractmethod
    def name(self) -> str:
        pass

    @property
    @abstractmethod
    def description(self) -> str:
        pass

    @abstractmethod
    async def process(self, context: Dict[str, Any]) -> Dict[str, Any]:
        """Execute agent task on structured context and return structured payload."""
        pass
