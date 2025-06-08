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
secret_id          036199ca-d3e9-87db-48c0-7fd2c766257c
secret_id_accessor 6a64946e-b3ad-b2ae-7fcb-54491761d1e1
secret_id_num_uses 10                                  
secret_id_ttl      720000                                

