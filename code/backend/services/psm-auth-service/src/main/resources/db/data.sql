insert into auth_identity_provider(
  tenant_id, provider_code, provider_type, provider_name, client_id, authorize_url, token_url, user_info_url, callback_url, user_mapping_field, config, enabled
) values (
  1,
  'demo-oidc',
  'OIDC',
  '演示OIDC身份源',
  'psm-demo-client',
  'https://idp.example.com/oauth2/authorize',
  'https://idp.example.com/oauth2/token',
  'https://idp.example.com/oauth2/userinfo',
  'http://localhost:18081/auth/sso/demo-oidc/callback',
  'sub',
  json_object('issuer', 'https://idp.example.com', 'scope', 'openid profile email'),
  0
) on duplicate key update provider_name=values(provider_name), updated_at=now();
