- [ ] **Auths release**
    - [ ] When we updated the bucket configuration, we must wire the response policy to verify whether it affects our changes.

- [ ] **Enhance release step**
    - [ ] When we do the release confirmation steps, test cases need to cover all the changes and some possible changes.
    - [ ] If there are corresponding logic modifications in a script, check for dependencies across the environment.
    - [ ] For the E2E test link, consider internal `curl` commands and the inflow of external traffic.
    - [ ] 整个 release 过程中，所有涉及到的一些组件或者事项都应该做一个单独的二次确认，比如涉及到 DNS 的修改。

- [ ] **How to fix ingress reverse IP issue**
    - [ ] Create a Confluence page using the recorder DNS response policy.
    - [ ] Sort our environment to specify which cases use this feature and add comments.
    - [ ] Write a script for recorder IP address, SVC IP address, ingress address, and DNS response policy.
