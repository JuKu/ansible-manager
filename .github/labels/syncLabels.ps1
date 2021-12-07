echo "Sync GitHub labels"

echo "GitHub Access Token: ";
$token = Read-Host "Please enter your GitHub access token"

echo "GitHub Access token: $token"

set GITHUB_ACCESS_TOKEN $token

github-label-sync -d -l labels.json JuKu/ansible-manager
PAUSE

github-label-sync -l labels.json JuKu/ansible-manager

PAUSE