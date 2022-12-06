# Log4j Scanner #

This repo is archived as of 6 Dec 2022 - and is no longer being maintained.

This repository provides a scanning solution for the log4j Remote Code Execution vulnerabilities (CVE-2021-44228 & CVE-2021-45046). 
The information and code in this repository is provided "as is" and was assembled with the help of the open-source community and updated by CISA through collaboration with the broader cybersecurity community. This is not intended to be a 100% true positive solution; False negatives may occur.

## Official CISA Guidance & Resources ##

- [CISA Apache Log4j Vulnerability Guidance](https://www.cisa.gov/uscert/apache-log4j-vulnerability-guidance)
- [Statement from CISA Director Easterly on “Log4j” Vulnerability](https://www.cisa.gov/news/2021/12/11/statement-cisa-director-easterly-log4j-vulnerability).

## CISA Current Activity Alerts ##

- [Apache Releases Log4j Version 2.15.0 to Address Critical RCE Vulnerability Under Exploitation](https://www.cisa.gov/uscert/ncas/current-activity/2021/12/10/apache-releases-log4j-version-2150-address-critical-rce)
- [CISA Creates Webpage for Apache Log4j Vulnerability CVE-2021-44228](https://www.cisa.gov/uscert/ncas/current-activity/2021/12/13/cisa-creates-webpage-apache-log4j-vulnerability-cve-2021-44228)

## CVE-2021-44228 & CVE-2021-45046 ##

### Steps to test ###

<details><summary>Configure your own DNS Server - Preferred) </summary><br/>
- Add DNS records to your domain. (example.com)

- `A` record with a value of your IP address (`test.example.com` -> <PUBLIC IP ADDRESS>)
- `NS` record (`ns1.example.com`) with a value of the `test.example.com` as chosen above.

- Host a DNS server to log DNS requests made to your domain. 

- Install the requirement modules -> `pip3 install -r requirements.txt`

- Modify the `dns/ddnsserver.py` script with the value of the NS record above (`test.example.com`) 

- `python3 ddnsserver.py --port 53 --udp >> dns-results.txt`

- Test it with `nslookup hello.test.example.com`. You can run `tail -f dns-results.txt` to monitor these logs. 

- You should see the entry in your `dns-results.txt` file after the `nslookup` command. Once you do, you're ready to scan! 

- Note: Same concepts will apply if you're using internal DNS to test this. 

</details>

<details><summary>DNS providers - (Interact.sh or canarytokens.org) </summary><br/>

- [Interact.sh](https://github.com/projectdiscovery/interactsh)  - Interactsh is an open-source solution for out-of-band data extraction. It is a tool designed to detect bugs that cause external interactions. These bugs include, Blind SQLi, Blind CMDi, SSRF, etc. 

- [Canarytokens.org](https://canarytokens.org/generate) - Canarytokens helps track activity and actions on your network.

</details>

<details><summary>LDAP Server (OPTIONAL)</summary><br/>

- Reference the `README.md` under the `ldap` directory if you'd also like to test a running LDAP server.

- Build the project using maven. `cd ldap`

- `mvn clean package -DskipTests`

- `nohup java -cp target/marshalsec-0.0.3-SNAPSHOT-all.jar marshalsec.jndi.LDAPRefServer "http://127.0.0.1:8080/#payload" 443 >> ldap_requests.txt &`

- There are [alternatives](https://github.com/alexandre-lavoie/python-log4rce) to this project as well. 
</details>

<details><summary>HTTP Service Discovery & Scanning</summary><br/>

- Gather your most update-to-date asset list of your organization and find web services. Though this vulnerability does not solely affect web services, 
this will serve as a great starting point to minimizing the attack surface.

- **If you have a list of company owned URLS, you may skip this step**: Utilize some well known tools like [httpprobe](https://github.com/tomnomnom/httprobe) or [httpx](https://github.com/projectdiscovery/httpx) to identify web services running on multiple ports. Basic Example: `httpprobe` -> `cat list-of-your-company-domains.txt | $HOME/go/bin/httprobe > your-web-assets.txt`

- Now that you have a list of URLs, you're ready to scan: `python3 log4j-scan.py --run-all-tests --custom-dns-callback-host test.example.com -l web-asset-urls.txt`

- Be sure to scan for the **new** CVE as well -> `python3 log4j-scan.py --test-CVE-2021-45046 --custom-dns-callback-host test.example.com -l web-asset-urls.txt`

- Monitor the DNS server configured in **Step 2**.
</details>

## CREDITS ##

As many in industry, we did not feel the need to "re-invent the wheel". This
recommended scanning solution is derived from the great work of others (with slight modifications). We've included two additional
projects to avoid using third-parties.

[log4-scanner](https://github.com/fullhunt/log4j-scan) - Log4j vulnerability scanning framework. **Thank you to the @fullhunt.io team.**

[dns](https://gist.github.com/pklaus/b5a7876d4d2cf7271873) - Simple DNS server (UDP and TCP) in Python. **Thank you @pklaus & @andreif.**

[ldap](https://github.com/mbechler/marshalsec) - Contains useful code to test the lookup() call. **Thank you @mbechler**


## Issues ##

If you have issues using the code, open an issue on the repository!

You can do this by clicking "Issues" at the top and clicking "New Issue" on the following page.

## Contributing ##

We welcome contributions!  Please see [here](CONTRIBUTING.md) for details.

## Disclaimers ##

- There are likely additional, as yet unknown ways to leverage these (**CVE-2021-44228** & **CVE-2021-45046**) vulnerabilities. CISA is staying vigilant across
multiple platforms (blog posts, repos, tweets, etc.) to stay up-to-date as the log4j situation unfolds and progresses.

- This repository will focus solely on providing tooling to help organizations look for a limited set of currently known vulnerabilities in assets owned by their organization.

- For CISA's official guidance on these vulnerabilities, please follow [this repository](https://github.com/cisagov/log4j-affected-db).

## License ##

The following attributions are referenced and/or derivative works distributed with this source: 

**log4j_scanner Copyright 2021 Mazin Ahmed**

**Java Unmarshaller Security Copyright 2021 Moritz Bechler**

MIT License Applicable to Original log4j_scanner and Java Unmarshaller Security Works: Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: 

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. 

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 

**Simple DNS Server Copyright 2021 Andrei Fokau**

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at: 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License. 

Any and all changes applied by CISA are in the public domain. 

## Legal Disclaimer ##

NOTICE

USE THIS SOFTWARE AT YOUR OWN RISK. THIS SOFTWARE COMES WITH NO WARRANTY, EITHER EXPRESS OR IMPLIED. THE UNITED STATES GOVERNMENT ASSUMES NO LIABILITY FOR THE USE OR MISUSE OF THIS SOFTWARE OR ITS DERIVATIVES.

THIS SOFTWARE IS OFFERED “AS-IS.” THE UNITED STATES GOVERNMENT WILL NOT INSTALL, REMOVE, OPERATE OR SUPPORT THIS SOFTWARE AT YOUR REQUEST. IF YOU ARE UNSURE OF HOW THIS SOFTWARE WILL INTERACT WITH YOUR SYSTEM, DO NOT USE IT.

## NO ENDORSEMENT ##
CISA does not endorse any commercial product or service, including any subjects of analysis. Any reference to specific commercial products, processes, or services by service mark, trademark, manufacturer, or otherwise, does not constitute or imply their endorsement, recommendation, or favoring by CISA.
