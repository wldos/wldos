export default {
  'store.contribute.title': 'Plugin contribution guide',
  'store.contribute.content':
    'Learn how to contribute your plugin to the WLDOS plugin ecosystem.',
  'store.contribute.overview.title': 'Contribution overview',
  'store.contribute.overview.alert.title':
    'The WLDOS plugin ecosystem adopts an open-source contribution model',
  'store.contribute.overview.alert.p1':
    'WLDOS is an open-source platform. To maintain the quality and security of the plugin ecosystem, all plugin contributions must go through the official review process.',
  'store.contribute.overview.alert.p2':
    'Developers can submit plugins via GitHub/GitLab and other code hosting platforms. After official review, plugins will be merged into the official repository and released with WLDOS.',
  'store.contribute.overview.alert.tipPrefix': 'Important reminder:',
  'store.contribute.overview.alert.tipText':
    'Users of third-party WLDOS instances are advised to use officially approved plugins and not to add plugins directly. This ensures the security, stability, and consistency of the plugin ecosystem.',
  'store.contribute.flow.title': 'Contribution process',
  'store.contribute.flow.step1.title': 'Develop plugin',
  'store.contribute.flow.step1.p1':
    'Develop your plugin according to the WLDOS plugin development guidelines, and make sure:',
  'store.contribute.flow.step1.item1':
    'Plugin functionality is complete and can run normally.',
  'store.contribute.flow.step1.item2':
    'Code quality is good and follows coding conventions.',
  'store.contribute.flow.step1.item3':
    'Includes a complete configuration file plugin.yml.',
  'store.contribute.flow.step1.item4':
    'Provides a clear README document.',
  'store.contribute.flow.step1.item5': 'Passes basic functional tests.',
  'store.contribute.flow.step2.title': 'Submit to code repository',
  'store.contribute.flow.step2.p1':
    'Submit plugin code to a code hosting platform (GitHub/GitLab, etc.):',
  'store.contribute.flow.step2.item1': 'Create a plugin project repository.',
  'store.contribute.flow.step2.item2':
    'Upload plugin source code and build artifacts.',
  'store.contribute.flow.step2.item3':
    'Write a detailed README documentation.',
  'store.contribute.flow.step2.item4':
    'Add the necessary license files.',
  'store.contribute.flow.step2.item5':
    'Create a release version (optional).',
  'store.contribute.flow.step3.title': 'Submit pull request',
  'store.contribute.flow.step3.p1':
    'Submit a pull request to the WLDOS official plugin repository:',
  'store.contribute.flow.step3.item1':
    'Fork the WLDOS official plugin repository.',
  'store.contribute.flow.step3.item2':
    'Add your plugin to the repository.',
  'store.contribute.flow.step3.item3':
    'Fill out detailed PR description (plugin functionality, usage scenarios, etc.).',
  'store.contribute.flow.step3.item4':
    'Ensure the code passes CI checks.',
  'store.contribute.flow.step3.item5': 'Wait for official review.',
  'store.contribute.flow.step3.link':
    'Visit the WLDOS official plugin repository',
  'store.contribute.flow.step4.title': 'Official review',
  'store.contribute.flow.step4.p1':
    'The WLDOS official team will review your plugin:',
  'store.contribute.flow.step4.item1': 'Code quality review.',
  'store.contribute.flow.step4.item2': 'Functional completeness testing.',
  'store.contribute.flow.step4.item3': 'Security checks.',
  'store.contribute.flow.step4.item4':
    'Compliance with guidelines and policies.',
  'store.contribute.flow.step4.item5': 'Documentation completeness check.',
  'store.contribute.flow.step4.alert.title': 'Review time',
  'store.contribute.flow.step4.alert.desc':
    'The review time is usually 1-3 working days. For complex plugins, it may take longer. We will communicate with you in time.',
  'store.contribute.flow.step5.title': 'Merge and release',
  'store.contribute.flow.step5.p1':
    'After passing the review, your plugin will be:',
  'store.contribute.flow.step5.item1':
    'Merged into the official plugin repository.',
  'store.contribute.flow.step5.item2':
    'Added to the WLDOS plugin marketplace.',
  'store.contribute.flow.step5.item3':
    'Released with the next WLDOS version.',
  'store.contribute.flow.step5.item4': 'Available to all WLDOS users.',
  'store.contribute.spec.title': 'Development guidelines',
  'store.contribute.spec.alert.title':
    'Plugin development must follow the following guidelines',
  'store.contribute.spec.section1.title': '1. Code conventions',
  'store.contribute.spec.section1.item1':
    'Follow Java and JavaScript coding conventions.',
  'store.contribute.spec.section1.item2':
    'Code comments should be clear, especially for key logic.',
  'store.contribute.spec.section1.item3':
    'Avoid hard-coding; use configuration files instead.',
  'store.contribute.spec.section1.item4':
    'Handle errors properly with appropriate exception handling.',
  'store.contribute.spec.section2.title': '2. Plugin configuration',
  'store.contribute.spec.section2.item1':
    'Must contain a valid plugin.yml configuration file.',
  'store.contribute.spec.section2.item2':
    'Plugin code (code) must be unique and follow naming conventions.',
  'store.contribute.spec.section2.item3':
    'Version numbers must follow semantic versioning (SemVer).',
  'store.contribute.spec.section2.item4':
    'Permission configuration should be clear and follow the principle of least privilege.',
  'store.contribute.spec.section3.title': '3. Security',
  'store.contribute.spec.section3.item1':
    'Must not contain malicious code or backdoors.',
  'store.contribute.spec.section3.item2':
    'API calls must perform permission checks.',
  'store.contribute.spec.section3.item3':
    'User input must be validated and filtered.',
  'store.contribute.spec.section3.item4':
    'Sensitive information must not be hard-coded in the code.',
  'store.contribute.spec.section4.title': '4. Documentation requirements',
  'store.contribute.spec.section4.item1':
    'Provide a complete README.md document.',
  'store.contribute.spec.section4.item2':
    'Explain plugin features, usage, and configuration options.',
  'store.contribute.spec.section4.item3':
    'Provide installation and uninstallation instructions.',
  'store.contribute.spec.section4.item4':
    'Include necessary screenshots or examples.',
  'store.contribute.standard.title': 'Review standards',
  'store.contribute.standard.item1.title': 'Functional completeness',
  'store.contribute.standard.item1.desc':
    'Plugin functionality is complete, runs properly, and has no obvious bugs.',
  'store.contribute.standard.item2.title': 'Code quality',
  'store.contribute.standard.item2.desc':
    'Code structure is clear, follows best practices, and is maintainable.',
  'store.contribute.standard.item3.title': 'Security',
  'store.contribute.standard.item3.desc':
    'Passes security review with no security vulnerabilities or risks.',
  'store.contribute.standard.item4.title': 'Compliance',
  'store.contribute.standard.item4.desc':
    'Complies with WLDOS plugin development guidelines and platform policies.',
  'store.contribute.standard.item5.title': 'Documentation completeness',
  'store.contribute.standard.item5.desc':
    'Documentation is clear and complete, enabling users to understand and use the plugin.',
  'store.contribute.faq.title': 'FAQ',
  'store.contribute.faq.q1.q':
    'Q: How long does it take to review my plugin?',
  'store.contribute.faq.q1.a':
    'A: Usually 1-3 working days. If the plugin is complex or changes are needed, it may take longer. We will communicate with you in time.',
  'store.contribute.faq.q2.q': 'Q: What if the review does not pass?',
  'store.contribute.faq.q2.a':
    'A: We will explain in detail the reasons for rejection and suggestions for improvement. You can modify based on the feedback and resubmit.',
  'store.contribute.faq.q3.q': 'Q: Can I submit commercial plugins?',
  'store.contribute.faq.q3.a':
    'A: Yes. Commercial plugins need to be clearly marked and provide a free trial version. Commercial licensing is handled by the developer.',
  'store.contribute.faq.q4.q':
    'Q: Can I update the plugin after it is released?',
  'store.contribute.faq.q4.a':
    'A: Yes. Submit a new pull request to update the plugin version. Updates must also go through the review process.',
  'store.contribute.faq.q5.q':
    'Q: How can I contact the official team?',
  'store.contribute.faq.q5.a':
    'A: You can contact us via GitHub issues, email (306991142@qq.com), or the official community.',
  'store.contribute.links.title': 'Related resources',
  'store.contribute.links.repo.title': 'WLDOS official plugin repository',
  'store.contribute.links.repo.desc':
    'View official plugin examples and submit your plugin.',
  'store.contribute.links.docs.title': 'Plugin development documentation',
  'store.contribute.links.docs.desc':
    'Detailed plugin development guides and API documentation.',
  'store.contribute.links.feedback.title': 'Feedback',
  'store.contribute.links.feedback.desc':
    'Submit issues, suggestions, or questions about plugin development.',
};

