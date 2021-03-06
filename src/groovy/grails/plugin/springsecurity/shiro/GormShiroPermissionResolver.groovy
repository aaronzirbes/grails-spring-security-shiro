/* Copyright 2013 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.springsecurity.shiro

import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class GormShiroPermissionResolver implements ShiroPermissionResolver {

	/** Dependency injection for the application. */
	def grailsApplication

	/* (non-Javadoc)
	 * @see grails.plugin.springsecurity.shiro.ShiroPermissionResolver#resolvePermissions(java.lang.String)
	 */
	Set<String> resolvePermissions(String username) {

		def conf = SpringSecurityUtils.securityConfig

		String permissionClassName = conf.shiro.permissionDomainClassName
		if (!permissionClassName) {
			throw new RuntimeException('No value specified for the Shiro permission class; ' +
				'set the grails.plugins.springsecurity.shiro.permissionDomainClassName attribute')
		}

		def dc = grailsApplication.getDomainClass(permissionClassName)
		if (!dc) {
			throw new RuntimeException("The specified permission domain class '$userClassName' is not a domain class")
		}

		Class<?> Permission = dc.clazz
		String usernamePropertyName = conf.userLookup.usernamePropertyName
		Permission.withCriteria {
			user {
				eq usernamePropertyName, username
			}
			projections {
				property 'permission'
			}
		}
	}
}
