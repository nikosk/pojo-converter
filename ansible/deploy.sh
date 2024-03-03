cd ..
./mill clean
./mill assembly
cd ansible 
pipenv shell
ansible-playbook -i inventory.ini playbook.yml
