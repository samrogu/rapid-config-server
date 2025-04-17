vault write auth/approle/role/configserver \
    token_policies="default,dev-policy" \
    token_ttl=1h \
    token_max_ttl=4h \
    secret_id_ttl=200h \
    secret_id_num_uses=10

vault read auth/approle/role/configserver/role-id
Key     Value                               
role_id ffa2d2fb-9f93-9475-c72f-2704a8e7edaf


vault write -f auth/approle/role/configserver/secret-id
Key                Value                               
secret_id          27a9fd76-a3ed-a06c-ae34-54171b68c5fc
secret_id_accessor 90e753d6-eda1-11f1-87ab-e62a05d35ecd
secret_id_num_uses 10                                  
secret_id_ttl      720000                                

