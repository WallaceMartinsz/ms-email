#!/bin/bash
set -e
echo "Verificando identidade de remetente no SES..."
awslocal ses verify-email-identity --email-address "${SES_SENDER:-no-reply@ms-email.local}"
echo "SES pronto."
