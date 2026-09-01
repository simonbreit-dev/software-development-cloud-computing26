{{/*
Expand the name of the chart.
*/}}
{{- define "sdfcc-backend.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "sdfcc-backend.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "sdfcc-backend.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels.
*/}}
{{- define "sdfcc-backend.labels" -}}
helm.sh/chart: {{ include "sdfcc-backend.chart" . }}
{{ include "sdfcc-backend.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels.
*/}}
{{- define "sdfcc-backend.selectorLabels" -}}
app.kubernetes.io/name: {{ include "sdfcc-backend.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use.
*/}}
{{- define "sdfcc-backend.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "sdfcc-backend.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Secret name used for envFrom.
*/}}
{{- define "sdfcc-backend.secretName" -}}
{{- default (printf "%s-secret" (include "sdfcc-backend.fullname" .)) .Values.secret.existingSecretName }}
{{- end }}

{{/*
Fail rendering instead of deploying a backend with missing database settings.
An existing Secret is validated by Kubernetes at runtime; generated Secret
values can and should be validated while rendering.
*/}}
{{- define "sdfcc-backend.validateValues" -}}
{{- if not .Values.secret.existingSecretName -}}
  {{- if not .Values.secret.enabled -}}
    {{- fail "secret.existingSecretName must be set when secret.enabled is false" -}}
  {{- end -}}
  {{- range $key := list "SPRING_DATASOURCE_URL" "SPRING_DATASOURCE_USERNAME" "SPRING_DATASOURCE_PASSWORD" -}}
    {{- if not (get $.Values.secret.data $key) -}}
      {{- fail (printf "secret.data.%s must be non-empty when the chart generates the database Secret" $key) -}}
    {{- end -}}
  {{- end -}}
{{- end -}}
{{- end }}
