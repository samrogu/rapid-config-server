vault write auth/approle/role/configserver \
    token_policies="default,dev-policy" \
    token_ttl=1h \
    token_max_ttl=4h \
    secret_id_ttl=60m \
    secret_id_num_uses=10

vault read auth/approle/role/configserver/role-id
Key     Value                               
role_id ffa2d2fb-9f93-9475-c72f-2704a8e7edaf


vault write -f auth/approle/role/configserver/secret-id
Key                Value                               
secret_id          9b5423e6-6587-3e88-0ab3-502615f6f214
secret_id_accessor 472e2b2a-09d6-1503-d038-c4da8f5d37a3
secret_id_num_uses 10                                  
secret_id_ttl      3600                                

