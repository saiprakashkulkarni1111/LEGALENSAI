# Data sources

| Source | Official URL | Retrieval | CAPTCHA |
| --- | --- | --- | --- |
| India Code | https://www.indiacode.nic.in | Indexed public statutory text + official handle URLs | Not used |
| Supreme Court | https://main.sci.gov.in | Indexed landmark judgments with official judgment URLs | Not bypassed |
| eCourts | https://services.ecourts.gov.in | Official-search workflow only | Never bypassed |
| NJDG | Official NJDG portal | Health + official search | Never bypassed |
| High Courts | Official HC portals | Official-search workflow | Never bypassed |

Automated retrieval is used only for publicly documented content already stored in adapters. If a live HTML portal is blocked, the API returns:

```json
{
  "status": "official_search_required",
  "official_url": "...",
  "reason": "Automated retrieval unavailable"
}
```
