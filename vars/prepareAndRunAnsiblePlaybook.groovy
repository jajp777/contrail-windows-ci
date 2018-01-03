def call(body) {
  def commonVars
  def ansibleConfig
  def vmConfig

  pipeline {
    agent none
    stages {
      stage('Prepare environment') {
        agent { label 'ansible' }
        environment {
          VC = credentials('vcenter')
        }
        steps {
          script {
            commonVars = evaluate readFile('jenkinsfiles/library/createCommonVars.groovy')
            ansibleConfig = evaluate readFile('jenkinsfiles/library/createAnsibleCfg.groovy')
            vmConfig = evaluate readFile(body.vmConfigScript)
          }

          dir('ansible') {
            script {
              commonVars.create(env.SHARED_DRIVE_IP, env.JENKINS_MASTER_IP)
              ansibleConfig.create(env.ANSIBLE_VAULT_KEY_FILE)
              vmConfig.create(env.VC_HOSTNAME, env.VC_DATACENTER, env.VC_CLUSTER, env.VC_FOLDER,
                              env.VC_NETWORK, env.VC_USR, env.VC_PSW, env.VM_TEMPLATE)
            }
            sh 'cp inventory.sample inventory'
            sh 'ansible-galaxy install -r requirements.yml -f'
          }
        }
      }
      stage('Run ansible') {
        agent { label 'ansible' }
        steps {
          dir('ansible') {
            ansiblePlaybook extras: '-e @vm.vars -e @common.vars', \
                            inventory: 'inventory', \
                            playbook: body.playbook, \
                            sudoUser: 'ubuntu'
          }
        }
      }
    }
  }
}
